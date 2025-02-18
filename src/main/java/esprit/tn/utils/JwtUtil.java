package esprit.tn.utils;

import esprit.tn.entities.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

public class JwtUtil {

    private static final String SECRET = "ken_mzelt_nekhdem_java_ala_haja_ghir_springboot_ndakhel_yidi_f_ecran_l_pc!!!!!!!";
    private static final Key SECRET_KEY = Keys.hmacShaKeyFor(Base64.getEncoder().encode(SECRET.getBytes()));

    private static final long EXPIRATION_TIME = 1000 * 60 * 60;


    public static String generateToken(int id, String username, Role role) {
        return Jwts.builder()
                .setSubject(username)
                .claim("id", id)
                .claim("role", role.name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(SECRET_KEY)
                .compact();
    }
    public static int getIdFromToken(String token) {
        Claims claims = validateToken(token);
        return claims.get("id", Integer.class); // Extracts the id
    }
    public static Role getRoleFromToken(String token) {
        Claims claims = validateToken(token);
        return Role.valueOf(claims.get("role", String.class)); // Convert string back to enum
    }

    public static Claims validateToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("Token expired!");
        } catch (JwtException e) {
            throw new RuntimeException("Invalid token!");
        }
    }
}
