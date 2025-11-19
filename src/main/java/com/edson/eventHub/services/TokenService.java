package com.edson.eventHub.services;

import com.edson.eventHub.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

/**
 * Serviço para manipulação de operações JWT (JSON Web Token).
 * Esta classe é responsável por gerar, analisar e validar tokens.
 */
@Service
public class TokenService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;
    
    /**
     * Extrai o nome de usuário (subject) de um token JWT.
     *
     * @param token O token JWT.
     * @return O nome de usuário contido no token.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Valida um token JWT em relação a um usuário.
     * Verifica se o nome de usuário do token corresponde ao e-mail do usuário e se o token não expirou.
     *
     * @param token O token JWT a ser validado.
     * @param user  O usuário para validação.
     * @return true se o token for válido, false caso contrário.
     */
    public boolean isTokenValid(String token, User user) {
        final String username = extractUsername(token);
        return (username.equals(user.getEmail())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extrai uma reivindicação (claim) específica de um token JWT.
     *
     * @param <T>            O tipo da reivindicação.
     * @param token          O token JWT.
     * @param claimsResolver Uma função para extrair a reivindicação do corpo de reivindicações.
     * @return A reivindicação extraída.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
    }

    private Key getSigningKey() {
        byte[] keyBytes = secret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Gera um novo token JWT para um determinado usuário.
     * O token inclui o e-mail do usuário como "subject" e seu ID e "role" como reivindicações personalizadas.
     *
     * @param user O usuário para o qual o token será gerado.
     * @return Um token JWT como uma String.
     */
    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("userId", user.getId())
                .claim("role", user.getRole().name())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
}