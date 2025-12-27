package com.pragma.powerup.infrastructure.input.rest;

import com.pragma.powerup.application.dto.request.UserRequestDto;
import com.pragma.powerup.application.handler.IUserHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserRestController {

    private final IUserHandler userHandler;

    @PostMapping("/owner")
    public ResponseEntity<Void> createOwner(@Valid @RequestBody UserRequestDto userRequestDto) {
        userHandler.saveOwner(userRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}