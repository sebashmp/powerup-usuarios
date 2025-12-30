package com.pragma.powerup.domain.api;

import com.pragma.powerup.domain.model.JwtModel;

public interface IAuthServicePort {
    JwtModel login(String email, String password);
}