package com.pragma.powerup.application.handler;

import com.pragma.powerup.application.dto.request.UserRequestDto;

public interface IUserHandler {
    void saveOwner(UserRequestDto userRequestDto);
}