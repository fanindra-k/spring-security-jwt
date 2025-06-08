package com.kushwaha.book.auth;

import com.kushwaha.book.token.TokenRepository;
import com.kushwaha.book.token.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class LogoutService implements LogoutHandler {

    private final TokenRepository tokenRepository;
    private final TokenService tokenService;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        try{
            System.out.println("LogoutService - Processing logout request");
            final String token = tokenService.extractTokenFromRequest(request);
            if(token == null || token.isEmpty()){
                System.out.println("LogoutService - No token found in request");
                return;
            }
            System.out.println("LogoutService - Found token, attempting to revoke"); // ← Add debug log
            tokenService.revokeToken(token, authentication);
            System.out.println("LogoutService - Token revoked successfully");
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }

    }
}
