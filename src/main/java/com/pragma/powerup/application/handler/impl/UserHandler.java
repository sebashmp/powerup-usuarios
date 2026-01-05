package com.pragma.powerup.application.handler.impl;

import com.pragma.powerup.application.dto.request.UserRequestDto;
import com.pragma.powerup.application.dto.response.UserResponseDto;
import com.pragma.powerup.application.handler.IUserHandler;
import com.pragma.powerup.application.mapper.IUserRequestMapper;
import com.pragma.powerup.application.mapper.IUserResponseMapper;
import com.pragma.powerup.domain.api.IUserServicePort;
import com.pragma.powerup.domain.model.UserModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserHandler implements IUserHandler {

    private final IUserServicePort userServicePort;
    private final IUserRequestMapper userRequestMapper;
    private final IUserResponseMapper userResponseMapper;

    @Override
    public void saveOwner(UserRequestDto userRequestDto) {
        UserModel userModel = userRequestMapper.toModel(userRequestDto);
        userServicePort.saveOwner(userModel);
    }
    @Override
    public UserResponseDto getUserById(Long id) {
        // Llamamos al dominio y mapeamos el resultado a un DTO de salida
        return userResponseMapper.toResponse(userServicePort.getUser(id));
    }

    @Override
    public void saveEmployee(UserRequestDto userRequestDto) {
        UserModel userModel = userRequestMapper.toModel(userRequestDto);
        userServicePort.saveEmployee(userModel);
    }

    @Override
    public void saveClient(UserRequestDto userRequestDto) {
        UserModel userModel = userRequestMapper.toModel(userRequestDto);
        userServicePort.saveClient(userModel);
    }
}