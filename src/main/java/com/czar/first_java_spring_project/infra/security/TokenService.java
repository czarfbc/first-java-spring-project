package com.czar.first_java_spring_project.infra.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.czar.first_java_spring_project.domain.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

@Service
public class TokenService {
    @Value("${api.security.token.secret}")
    private String secretTokenKey;

    public Map<String, String> generateToken(User user) {
        try{
            Algorithm algorithm = Algorithm.HMAC256(this.secretTokenKey);

            String token;
            token = JWT.create()
                    .withIssuer("first-app-java-spring")
                    .withSubject(user.getEmail())
                    .withClaim("type", "accessToken")
                    .withExpiresAt(this.generateExpireDate(2))
                    .sign(algorithm);

            String refreshToken;
            refreshToken = JWT.create()
                    .withIssuer("first-app-java-spring")
                    .withSubject(user.getEmail())
                    .withClaim("type", "refreshToken")
                    .withExpiresAt(this.generateExpireDate(168))
                    .sign(algorithm);

            Map<String, String> tokens = new HashMap<>();
            tokens.put("accessToken", token);
            tokens.put("refreshToken", refreshToken);

            return tokens;
        }
        catch (JWTCreationException exception){
            throw new RuntimeException("Error while authenticating");
        }
    }

    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(this.secretTokenKey);

            String validate;
            validate = JWT.require(algorithm)
                    .withIssuer("first-app-java-spring")
                    .build()
                    .verify(token)
                    .getSubject();

            return validate;
        } catch (JWTVerificationException exception) {
            return null;
        }
    }

    private Instant generateExpireDate(int hours) {
        return LocalDateTime.now().plusHours(hours).toInstant(ZoneOffset.of("-3"));
    }
}
