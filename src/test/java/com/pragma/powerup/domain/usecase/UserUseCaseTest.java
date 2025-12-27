package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.exception.DomainException;
import com.pragma.powerup.domain.model.UserModel;
import com.pragma.powerup.domain.spi.IUserPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserUseCaseTest {

    @Mock
    private IUserPersistencePort userPersistencePort;

    @InjectMocks
    private UserUseCase userUseCase;

    private UserModel userModel;

    @BeforeEach
    void setUp() {
        userModel = new UserModel();
        userModel.setName("Sebastian");
        userModel.setLastName("Hidalgo");
        userModel.setIdDocument("123456789");
        userModel.setPhone("+573001234567");
        userModel.setBirthDate(LocalDate.now().minusYears(20)); // 20 años
        userModel.setEmail("test@mail.com");
    }

    @Test
    @DisplayName("Should save owner when all validations pass")
    void saveOwner_Success() {
        // Arrange
        when(userPersistencePort.existsByEmail(anyString())).thenReturn(false);
        // Act
        userUseCase.saveOwner(userModel);
        // Assert
        verify(userPersistencePort, times(1)).saveUser(userModel);
    }

    @Test
    @DisplayName("Should throw exception when user is underage")
    void saveOwner_Underage_ThrowsException() {
        // Arrange
        userModel.setBirthDate(LocalDate.now().minusYears(15)); // 15 años
        // Act & Assert
        DomainException exception = assertThrows(DomainException.class, () -> userUseCase.saveOwner(userModel));
        assertEquals("The user must be an adult (18+ years old).", exception.getMessage());
        verify(userPersistencePort, never()).saveUser(any());
    }

    @Test
    @DisplayName("Should throw exception when document is not numeric")
    void saveOwner_InvalidDocument_ThrowsException() {
        // Arrange
        userModel.setIdDocument("12345A78");
        // Act & Assert
        DomainException exception = assertThrows(DomainException.class, () -> userUseCase.saveOwner(userModel));
        assertEquals("ID Document must be purely numeric.", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when email already exists")
    void saveOwner_EmailExists_ThrowsException() {
        // Arrange
        when(userPersistencePort.existsByEmail(anyString())).thenReturn(true);
        // Act & Assert
        assertThrows(DomainException.class, () -> userUseCase.saveOwner(userModel));
    }
}