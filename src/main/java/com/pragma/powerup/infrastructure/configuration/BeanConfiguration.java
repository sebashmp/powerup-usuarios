package com.pragma.powerup.infrastructure.configuration;

import com.pragma.powerup.domain.api.IUserServicePort;
import com.pragma.powerup.domain.spi.IUserPersistencePort;
import com.pragma.powerup.domain.usecase.UserUseCase;
import com.pragma.powerup.infrastructure.out.jpa.adapter.UserJpaAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.pragma.powerup.infrastructure.out.jpa.mapper.IUserEntityMapper;
import com.pragma.powerup.infrastructure.out.jpa.repository.IUserRepository;

@Configuration
@RequiredArgsConstructor
public class BeanConfiguration {
    private final IUserRepository userRepository;
    private final IUserEntityMapper userEntityMapper;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public IUserPersistencePort userPersistencePort() {
        return new UserJpaAdapter(userRepository, userEntityMapper);
    }

    @Bean
    public IUserServicePort userServicePort() {
        return new UserUseCase(userPersistencePort());
    }
}