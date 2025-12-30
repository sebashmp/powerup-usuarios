package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.IAuthServicePort;
import com.pragma.powerup.domain.exception.DomainException;
import com.pragma.powerup.domain.model.JwtModel;
import com.pragma.powerup.domain.model.UserModel;
import com.pragma.powerup.domain.spi.IPasswordEncoderPort;
import com.pragma.powerup.domain.spi.ITokenEncoderPort;
import com.pragma.powerup.domain.spi.IUserPersistencePort;

public class AuthUseCase implements IAuthServicePort {

    private final IUserPersistencePort userPersistencePort;
    private final IPasswordEncoderPort passwordEncoderPort;
    private final ITokenEncoderPort tokenEncoderPort;

    public AuthUseCase(IUserPersistencePort userPersistencePort,
                       IPasswordEncoderPort passwordEncoderPort,
                       ITokenEncoderPort tokenEncoderPort) {
        this.userPersistencePort = userPersistencePort;
        this.passwordEncoderPort = passwordEncoderPort;
        this.tokenEncoderPort = tokenEncoderPort;
    }

    @Override
    public JwtModel login(String email, String password) {
        // 1. Buscar usuario
        UserModel user = userPersistencePort.findByEmail(email);
        if (user == null) {
            throw new DomainException("Invalid credentials.");
        }

        // 2. Validar contraseña (La lógica de "intentos ilimitados" simplifica no tener un contador aquí)
        if (!passwordEncoderPort.matches(password, user.getPassword())) {
            throw new DomainException("Invalid credentials.");
        }

        // 3. Generar el token a través del puerto SPI
        String token = tokenEncoderPort.generateToken(user);
        return new JwtModel(token);
    }
}