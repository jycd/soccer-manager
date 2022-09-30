package com.toptal.soccermanager.configuration.authentication;

import com.toptal.soccermanager.model.dto.UserDto;
import com.toptal.soccermanager.model.entity.User;
import com.toptal.soccermanager.repository.UserRepository;
import com.toptal.soccermanager.utils.UserInfo;
import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.TextCodec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Slf4j
public class JwtUtils {
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private int jwtExpirationInSeconds;

    @Autowired
    private UserRepository userRepository;

    public String getJwtToken(UserDto user) {
        Date now = new Date();
        Date exp = new Date(System.currentTimeMillis() + (1000L * jwtExpirationInSeconds));

        String jwt = Jwts.builder()
                .setSubject(user.getEmail())
                .claim("id", user.getId())
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(
                        SignatureAlgorithm.HS512,
                        TextCodec.BASE64.decode(jwtSecret)
                )
                .compact();
        return jwt;
    }

    public UserInfo getUserFromJwtToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();

        long id = claims.get("id", Long.class);
        User user = userRepository.findById(id)
                .orElse(null);

        if (user == null) {
            return null;
        }
        return new UserInfo(user);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException
                 | SignatureException | IllegalArgumentException ex) {
            log.error("Token validation error" + ex.getMessage());
        }
        return false;
    }
}
