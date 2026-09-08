package org.texas.computerecommerce.Security;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
@Component
public class Jwtutil {
    @Autowired
    private CustomUserDetailsService userDetailsService;
@Value("${jwt.secret}")
private String secreteString;
    @Value("${jwt.expiration}")
private Long expirationTime;

    public SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(secreteString.getBytes(StandardCharsets.UTF_8));
    }

public String generateToken(String email, String userId) {
        return Jwts.builder()
                .subject(email)
                .claim("userId",userId.toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis()+expirationTime))
                .signWith(getSecretKey())
                .compact();
}

    public String getUserEmailFromToken(String token) {
        if (token != null) {
            token = token.trim();
        }
        Claims claims= Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();

    }
}
