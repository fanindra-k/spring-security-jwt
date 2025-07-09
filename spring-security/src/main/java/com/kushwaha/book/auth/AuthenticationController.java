package com.kushwaha.book.auth;

import com.kushwaha.book.handler.ExceptionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name="Authentication")
@CrossOrigin(origins="http://127.0.0.1:5500")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "Register a new user")
    public ResponseEntity<?> register(
            @RequestBody @Valid RegistrationRequest request
    ) throws MessagingException {
        authenticationService.register(request);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/register-instructor")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "Register a new user")
    public ResponseEntity<?> registerInstructor(
            @RequestBody @Valid RegistrationRequest request
    ) throws MessagingException {
        authenticationService.registerInstructor(request);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/authenticate")
    @Operation(summary = "Authenticate user and return JWT token")
    public ResponseEntity<AuthenticateResponse> authenticate(
            @RequestBody @Valid AuthenticateRequest request
    ){
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    @GetMapping("/activate-account")
    @Operation(summary = "Activate user account using token")
    public AuthenticateResponse confirm(
            @RequestParam String token
    ) throws MessagingException {
        return authenticationService.activateAccount(token);
    }
}
