package com.pragma.powerup.application.handler;

import com.pragma.powerup.application.dto.request.LoginRequestDto;
import com.pragma.powerup.application.dto.response.JwtResponseDto;

public interface IAuthHandler {
    JwtResponseDto login(LoginRequestDto loginRequestDto);
}