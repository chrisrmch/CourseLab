package es.courselab.app.jwt;

import es.courselab.app.model.User;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecureDigestAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtils {
    @Value("${courselab.app.jwtSecret}")
    private String jwtSecret;

    @Value("${courselab.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateJwtToken(Authentication authentication) {
        SecureDigestAlgorithm<SecretKey, ?> alg = Jwts.SIG.HS256;
        User accountPrincipal = (User) authentication.getPrincipal();

        return Jwts
                .builder()
                .subject((accountPrincipal.getUsername()))
                .issuedAt(new Date()).expiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(getSignInKey(), alg)
                .compact();
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts
                .parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().verifyWith(getSignInKey()).build().parse(authToken);
            return true;
        } catch (MalformedJwtException | IllegalArgumentException | UnsupportedJwtException | ExpiredJwtException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }
}