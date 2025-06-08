package com.kushwaha.book.security;


import com.kushwaha.book.exceptions.InvalidTokenException;
import com.kushwaha.book.exceptions.TokenExpiredException;
import com.kushwaha.book.token.TokenRepository;
import com.kushwaha.book.token.TokenService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@Slf4j
public class JwtService {
    private final TokenRepository tokenRepository;
    private final TokenService tokenService;
    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;
    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    public JwtService(TokenRepository tokenRepository, TokenService tokenService) {
        this.tokenRepository = tokenRepository;
        this.tokenService = tokenService;
    }


    public String extractUserName(String token) {
        try{
            return extractClaim(token, Claims::getSubject);
        }
        catch(Exception e){
            log.error("Error extracting username from token: {}", e.getMessage());
            throw new InvalidTokenException("Invalid token");
        }
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        try{
            final Claims claims = extractAllClaims(token);
            return claimsResolver.apply(claims);
        }
        catch(Exception e){
            log.error("Error extracting claims from token: {}", e.getMessage());
            throw new InvalidTokenException("Invalid token");
        }
    }

    private Claims extractAllClaims(String token) {
        try{
            return Jwts
                    .parser()
                    .verifyWith(getSignInKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            log.debug("JWT token is expired: {}", e.getMessage());
            throw new TokenExpiredException("Token has expired");
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
            throw new InvalidTokenException("Unsupported token format");
        } catch (MalformedJwtException e) {
            log.error("JWT token is malformed: {}", e.getMessage());
            throw new InvalidTokenException("Malformed token");
        } catch (SignatureException e) {
            log.error("JWT signature validation failed: {}", e.getMessage());
            throw new InvalidTokenException("Invalid token signature");
        } catch (IllegalArgumentException e) {
            log.error("JWT token is illegal: {}", e.getMessage());
            throw new InvalidTokenException("Invalid token");
        }

    }


    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String,Object> claims, UserDetails userDetails) {
        return buildToken(claims, userDetails, jwtExpiration);
    }

    /**
     * Build JWT token with specified expiration
     */
    private String buildToken(Map<String, Object> claims, UserDetails userDetails, long jwtExpiration) {
        var authorities = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .claim("authorities", authorities)
                .signWith(getSignInKey())
                .compact();
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    /**
     * Check if token is valid for the given user
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String userName = extractUserName(token);
        return (userName.equals(userDetails.getUsername())) && !isTokenExpired(token) && !isTokenRevoked(token);
    }

    public boolean isTokenRevoked(String token) {
        try{
            return tokenRepository.findByToken(token)
                    .map(tokenEntity -> tokenEntity.isRevoked()).orElse(false);
        }catch (Exception e) {
            log.error("Error checking token revocation status", e);
            return true;
        }
    }

    private boolean isTokenExpired(String token) {

        try{
            return extractExpiration(token).before(new Date());
        }
        catch (ExpiredJwtException e) {
            log.debug("Token is expired: {}", e.getMessage());
            return true;
        } catch (Exception e) {
            log.warn("Error checking token expiration: {}", e.getMessage());
            return true;
        }
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }


}
