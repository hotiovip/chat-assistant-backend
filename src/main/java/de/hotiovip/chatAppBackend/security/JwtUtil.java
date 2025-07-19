package de.hotiovip.chatAppBackend.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secretString;
    @Value("${jwt.expirationTime}")
    private long expirationTime;

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);
    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        byte[] decodedKey = Base64.getDecoder().decode(secretString);
        secretKey = Keys.hmacShaKeyFor(decodedKey);
    }

    public String generateToken(Long id) {
        return Jwts.builder()
                .setSubject(id.toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(secretKey)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            logger.info("Token expired: {}", e.getMessage());
            return false;
        } catch (JwtException e) {
            logger.info("Invalid token: {}", e.getMessage());
            return false;
        }
    }

    public Optional<Long> getTokenSubject(String token) {
        if (validateToken(token)) {
            String subject = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();

            return Optional.of(Long.parseLong(subject));
        }
        else {
            return Optional.empty();
        }
    }
}
