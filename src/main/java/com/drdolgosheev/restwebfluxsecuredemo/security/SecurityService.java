package com.drdolgosheev.restwebfluxsecuredemo.security;

import com.drdolgosheev.restwebfluxsecuredemo.entity.UserEntity;
import com.drdolgosheev.restwebfluxsecuredemo.exceptions.AuthException;
import com.drdolgosheev.restwebfluxsecuredemo.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.*;

@Component
@RequiredArgsConstructor
public class SecurityService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.issuer}")
    private String issuer;

    @Value("${jwt.expiration}")
    private Integer expirationInSeconds;

    private TokenDetails generateToken(UserEntity user) {
        Map<String, Object> claims = new HashMap<>(){{
            put("role", user.getRole());
        }};

        return generateToken(claims, user.getId().toString());
    }

    private TokenDetails generateToken(Map<String, Object> claims, String subject) {
        Long expirationTimeInMiles = expirationInSeconds * 1000L;
        Date expirationDate = new Date(new Date().getTime() + expirationTimeInMiles);

        return generateToken(expirationDate, claims, subject);
    }

    private TokenDetails generateToken(Date expirationDate, Map<String, Object> claims, String subject) {
        Date createdDate = new Date();

        String token = Jwts.builder()
                .setClaims(claims)
                .setExpiration(expirationDate)
                .setSubject(subject)
                .setIssuedAt(createdDate)
                .setId(UUID.randomUUID().toString())
                .setIssuer(issuer)
                .signWith(SignatureAlgorithm.HS256, Base64.getEncoder().encodeToString(secret.getBytes()))
                .compact();

        return TokenDetails.builder()
                .token(token)
                .issueAt(createdDate)
                .expireAt(expirationDate)
                .build();
    }

    public Mono<TokenDetails> authenticate(String username, String password){
        return userRepository.findByUsername(username).flatMap(user -> {

            if (!user.isEnabled())
                return Mono.error(new AuthException("Account disabled", "AC001"));

            if (!passwordEncoder.matches(password, user.getPassword()))
                return Mono.error(new AuthException("Invalid password", "AC002"));

            return Mono.just(generateToken(user).toBuilder()
                    .userId(user.getId())
                    .build());
        }).switchIfEmpty(Mono.error(new AuthException("Invalid login", "AC003")));
    }
}
