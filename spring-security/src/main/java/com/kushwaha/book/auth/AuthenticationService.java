package com.kushwaha.book.auth;

import com.kushwaha.book.email.EmailService;
import com.kushwaha.book.email.EmailTemplateName;
import com.kushwaha.book.exceptions.UserAlreadyExistException;
import com.kushwaha.book.role.Role;
import com.kushwaha.book.role.RoleRepository;
import com.kushwaha.book.security.JwtService;
import com.kushwaha.book.token.Token;
import com.kushwaha.book.token.TokenRepository;
import com.kushwaha.book.token.TokenService;
import com.kushwaha.book.user.*;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final ActivationCodeRepository activationCodeRepository;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Value("${application.mailing.frontend.activation-url}")
    private String activationURL;

    public void registerInstructor(@Valid RegistrationRequest request) throws MessagingException {
        register(request, "INSTRUCTOR");
    }
    public void register(RegistrationRequest request) throws MessagingException{
        register(request, "USER");
    }

    public void register(RegistrationRequest request, String roleName) throws MessagingException {
        if(userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistException("User with this email is already exist");
        }
        var userRole = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("USER ROLE not found"));
        var user = User.builder()
                .firstName(request.getFirstName().trim())
                .lastName(request.getLastName().trim())
                .email(request.getEmail().trim())
                .password(passwordEncoder.encode(request.getPassword()))
                .accountLocked(false)
                .enabled(false)
                .roles(Set.of(userRole))
                .build();
        userRepository.save(user);
        sendValidationEmail(user);
    }

    private void sendValidationEmail(User user) throws MessagingException {
        var activationCode = generateAndSaveActivationCode(user);

        emailService.sendEmail(
                user.getEmail(),
                user.fullName(),
                EmailTemplateName.ACTIVATE_ACCOUNT,
                activationURL,
                activationCode,
                "Account Activation"
        );
    }

    private String generateAndSaveActivationCode(User user) {
        // generate activation code
        String generate = generateActivationCode(6);
        var activationCode = ActivationCode.builder()
                .code(generate)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();
        activationCodeRepository.save(activationCode);
        return generate;
    }

    private String generateActivationCode(int length) {
        String characters = "0123456789";
        StringBuilder activationCode = new StringBuilder();
        SecureRandom random = new SecureRandom();
        for(int i=0; i<length; i++) {
            int randomIndex = random.nextInt(characters.length()); // 0 - 9
            activationCode.append(characters.charAt(randomIndex));
        }
        return activationCode.toString();
    }

    public AuthenticateResponse authenticate(@Valid AuthenticateRequest request) {
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var claims = new HashMap<String, Object>();
        var user = ((User)auth.getPrincipal());
        claims.put("fullName", user.fullName());
        var jwtToken = jwtService.generateToken(claims, user);
        tokenService.saveToken(jwtToken,user);
        return AuthenticateResponse
                .builder()
                .username(user.getEmail())
                .rolename(user.getRoles().stream().map(Role::getName).collect(Collectors.toList()))
                .token(jwtToken)

                .build();
    }
//    @Transactional
    public void activateAccount(String code) throws MessagingException {
        var savedActivationCode = activationCodeRepository.findByCode(code).orElseThrow(() -> new RuntimeException("Invalid token"));
        if(LocalDateTime.now().isAfter(savedActivationCode.getExpiresAt())){
            sendValidationEmail(savedActivationCode.getUser());
            throw new RuntimeException("Activation token expired. A new Activation token has been sent");
        }
        var user = userRepository.findById(savedActivationCode.getUser().getId())
                .orElseThrow(() -> new UsernameNotFoundException("User name not found"));
        user.setEnabled(true);
        userRepository.save(user);
        savedActivationCode.setValidatedAt(LocalDateTime.now());
        activationCodeRepository.save(savedActivationCode);
    }

}
