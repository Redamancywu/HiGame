package com.higame.security;

import com.higame.config.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenProvider {
    
    @Autowired
    private JwtConfig jwtConfig;
    
    private SecretKey getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(jwtConfig.getBase64Secret());
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        
        Date now = new Date();
        Date expiration = new Date(now.getTime() + jwtConfig.getAccessTokenExpiration() * 1000);
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuer(jwtConfig.getIssuer())
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(getSigningKey())
                .compact();
    }

    public Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    public String getRoleFromToken(String token) {
        return getClaimsFromToken(token).get("role", String.class);
    }
}
