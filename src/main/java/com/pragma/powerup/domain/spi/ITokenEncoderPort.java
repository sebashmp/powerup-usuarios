package com.pragma.powerup.domain.spi;

import com.pragma.powerup.domain.model.UserModel;

public interface ITokenEncoderPort {
    String generateToken(UserModel user);
}