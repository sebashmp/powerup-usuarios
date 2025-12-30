package com.pragma.powerup.application.handler.impl;

import com.pragma.powerup.application.dto.request.LoginRequestDto;
import com.pragma.powerup.application.dto.response.JwtResponseDto;
import com.pragma.powerup.application.handler.IAuthHandler;
import com.pragma.powerup.domain.api.IAuthServicePort;
import com.pragma.powerup.domain.model.JwtModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthHandler implements IAuthHandler {
    private final IAuthServicePort authServicePort;

    @Override
    public JwtResponseDto login(LoginRequestDto loginRequestDto) {
        JwtModel jwtModel = authServicePort.login(loginRequestDto.getEmail(), loginRequestDto.getPassword());
        return new JwtResponseDto(jwtModel.getToken());
    }
}