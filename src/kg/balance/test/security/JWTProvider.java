package kg.balance.test.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JWTProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.tokenTTL}")
    private int tokenTTL;

    public Long getUserId (String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token).getBody();
        return Long.parseLong(claims.getSubject());
    }

    public String generateToken (Long userId) {
        Date now = new Date();
        Date expirationDate = new Date (now.getTime() + tokenTTL);
        return Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS256, jwtSecret)
                .compact();
    }
}
