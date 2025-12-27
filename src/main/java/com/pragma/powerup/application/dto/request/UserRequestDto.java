package com.pragma.powerup.application.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Getter
@Setter
public class UserRequestDto {
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Document is required")
    @Pattern(regexp = "\\d+", message = "Document must be only numbers")
    private String idDocument;

    @NotBlank(message = "Phone is required")
    @Size(max = 13, message = "Phone must be at most 13 characters")
    private String phone;

    @NotNull(message = "Birth date is required")
    private LocalDate birthDate;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;
}