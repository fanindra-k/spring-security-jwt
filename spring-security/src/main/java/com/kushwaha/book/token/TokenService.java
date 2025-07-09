package com.kushwaha.book.token;

import com.kushwaha.book.user.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
@Slf4j
public class TokenService {
    private final TokenRepository tokenRepository;

//    @Transactional
    public void revokeToken(String jwtToken, Authentication authentication) {
//       tokenRepository.findByToken(jwtToken)
//               .ifPresentOrElse(token -> {
//                   token.setRevoked(true);
//                   tokenRepository.save(token);
//                   log.info("Token revoked for user: {}",
//                           authentication != null ? authentication.getName() : "unknown");
//               },
//                       () -> log.warn("Attempted to revoke non-existent token")
//               );
        var storedToken = tokenRepository.findByToken(jwtToken)
                .orElse(null);
        if (storedToken != null) {
            storedToken.setExpired(true);
            storedToken.setRevoked(true);
            tokenRepository.save(storedToken);
            SecurityContextHolder.clearContext();
        }
    }
    public String extractTokenFromRequest(HttpServletRequest request) {
        final String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }
        return null;
    }


    @Transactional
    public void saveToken(String token, User user) {

        try {
            var jwtToken = Token.builder()
                    .token(token)
                    .user(user)
                    .tokenType(TokenType.BEARER)
                    .createdAt(LocalDateTime.now())
                    .expired(false)
                    .revoked(false)
                    .build();
            tokenRepository.save(jwtToken);
            log.debug("Token saved for user: {}", user.getUsername());
        } catch (Exception e) {
            log.error("Error saving token for user: {}", user.getUsername(), e);
            throw new RuntimeException("Failed to save token", e);
        }
    }

    public void revokeAllUserTokens(User user) {
        try {
            tokenRepository.revokeAllUserToken(user.getId(), LocalDateTime.now());
        } catch (Exception e) {
            log.error("Error revoking tokens for user: {}", user.getUsername(), e);
            throw new RuntimeException("Failed to revoke user tokens", e);
        }
    }
}
