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

@Service
public class TokenService {
    @Value("${api.security.token.secret}")
    private String secretTokenKey;

    public String generateToken(User user) {
        try{
            Algorithm algorithm = Algorithm.HMAC256(this.secretTokenKey);

            String token;
            token = JWT.create()
                    .withIssuer("first-app-java-spring")
                    .withSubject(user.getEmail())
                    .withExpiresAt(this.generateExpireDate())
                    .sign(algorithm);

            return token;
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

    private Instant generateExpireDate() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-3"));
    }
}
