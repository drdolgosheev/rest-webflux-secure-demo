package com.drdolgosheev.restwebfluxsecuredemo.security;

import com.drdolgosheev.restwebfluxsecuredemo.entity.UserEntity;
import com.drdolgosheev.restwebfluxsecuredemo.exceptions.InvalidTokenException;
import com.drdolgosheev.restwebfluxsecuredemo.repository.UserRepository;
import com.drdolgosheev.restwebfluxsecuredemo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AuthenticationManager implements ReactiveAuthenticationManager {

    private final UserService userService;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        CustomPrincipal principal = (CustomPrincipal) authentication.getPrincipal();
        return userService.getUserById(principal.getId())
                .filter(UserEntity::isEnabled)
                .switchIfEmpty(Mono.error(new InvalidTokenException("User disabled")))
                .map(user -> authentication);
    }
}
