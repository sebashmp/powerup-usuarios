package com.pragma.powerup.application.handler.impl;

import com.pragma.powerup.application.dto.request.UserRequestDto;
import com.pragma.powerup.application.mapper.IUserRequestMapper;
import com.pragma.powerup.domain.api.IUserServicePort;
import com.pragma.powerup.domain.model.UserModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserHandlerTest {

    @Mock
    private IUserServicePort userServicePort;
    @Mock
    private IUserRequestMapper userRequestMapper;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserHandler userHandler;

    @Test
    @DisplayName("Should encrypt password and set owner role before calling service")
    void saveOwner_FlowTest() {
        // Arrange
        UserRequestDto dto = new UserRequestDto();
        dto.setPassword("plainTextPassword");

        UserModel userModel = new UserModel();
        userModel.setPassword("plainTextPassword");

        when(userRequestMapper.toModel(any(UserRequestDto.class))).thenReturn(userModel);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        // Act
        userHandler.saveOwner(dto);

        // Assert
        verify(passwordEncoder).encode("plainTextPassword");
        verify(userServicePort).saveOwner(argThat(model ->
                model.getPassword().equals("encodedPassword") &&
                        model.getRole().getId().equals(2L)
        ));
    }
}