package com.drdolgosheev.restwebfluxsecuredemo.rest;

import com.drdolgosheev.restwebfluxsecuredemo.dto.AuthRequestDto;
import com.drdolgosheev.restwebfluxsecuredemo.dto.AuthResponseDto;
import com.drdolgosheev.restwebfluxsecuredemo.dto.UserDto;
import com.drdolgosheev.restwebfluxsecuredemo.entity.UserEntity;
import com.drdolgosheev.restwebfluxsecuredemo.mapper.UserMapper;
import com.drdolgosheev.restwebfluxsecuredemo.security.CustomPrincipal;
import com.drdolgosheev.restwebfluxsecuredemo.security.SecurityService;
import com.drdolgosheev.restwebfluxsecuredemo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthRestControllerV1 {
    private final SecurityService securityService;
    private final UserService userService;
    private final UserMapper userMapper;


    @PostMapping("/register")
    public Mono<UserDto> register(@RequestBody UserDto dto) {
        UserEntity entity = userMapper.map(dto);
        return userService.registerUser(entity)
                .map(userMapper::map);
    }

    @PostMapping("/login")
    public Mono<AuthResponseDto> login(@RequestBody AuthRequestDto dto) {
        return securityService.authenticate(dto.getUsername(), dto.getPassword())
                .flatMap(tokenDetails -> Mono.just(
                        AuthResponseDto.builder()
                                .userId(tokenDetails.getUserId())
                                .token(tokenDetails.getToken())
                                .issuedAt(tokenDetails.getIssueAt())
                                .expiresAt(tokenDetails.getExpireAt())
                                .build()
                ));
    }

    @GetMapping("/info")
    public Mono<UserDto> getUserInfo(Authentication authentication) {
        CustomPrincipal customPrincipal = (CustomPrincipal) authentication.getPrincipal();

        return userService.getUserById(customPrincipal.getId())
                .map(userMapper::map);
    }
}
