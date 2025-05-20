package com.example.riberpublicfichajeapi.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.*;

import java.util.Date;
import java.util.List;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration-ms}")
    private long defaultExpiracion;

    @Value("${jwt.expiration-recuerdame-ms}")
    private long recuerdaExpiracion;

    private final UserDetailsService userDetailsService;

    public JwtTokenProvider(UserDetailsService uds) {
        this.userDetailsService = uds;
    }

    /**
     * Crea el token con las especificaciones recibidas.
     *
     * @param username nombre del usuario
     * @param recuerdame boolean para extender el Token
     * @return devuelve el token
     */
    public String createToken(String username, List<String> roles, boolean recuerdame) {
        long now = System.currentTimeMillis();
        long exp = now + (recuerdame ? recuerdaExpiracion : defaultExpiracion);
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("roles", roles);

        return Jwts.builder()
                .setSubject(username)
                .claim("authorities", roles)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(exp))
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes())
                .compact();
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(getUsername(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getUsername(String token) {
        return Jwts.parser().setSigningKey(secretKey.getBytes())
                .parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey.getBytes())
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
