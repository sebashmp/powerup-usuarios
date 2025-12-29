package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.exception.DomainException;
import com.pragma.powerup.domain.model.RoleModel;
import com.pragma.powerup.domain.model.UserModel;
import com.pragma.powerup.domain.spi.IPasswordEncoderPort;
import com.pragma.powerup.domain.spi.IRolePersistencePort;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserUseCaseTest {

    @Mock
    private IUserPersistencePort userPersistencePort;

    @Mock
    private IRolePersistencePort rolePersistencePort;

    @Mock
    private IPasswordEncoderPort passwordEncoderPort;

    @InjectMocks
    private UserUseCase userUseCase;

    private UserModel userModel;
    private RoleModel roleModel;

    @BeforeEach
    void setUp() {
        roleModel = new RoleModel();
        roleModel.setId(2L);
        roleModel.setName("ROLE_PROPIETARIO");

        userModel = new UserModel();
        userModel.setName("Sebastian");
        userModel.setLastName("Hidalgo");
        userModel.setIdDocument("123456789");
        userModel.setPhone("+573001234567");
        userModel.setBirthDate(LocalDate.now().minusYears(20));
        userModel.setEmail("test@mail.com");
    }

    @Test
    @DisplayName("Should save owner when all validations pass")
    void saveOwner_Success() {
        // Arrange
        String plainPassword = "password123";
        String encryptedPassword = "encrypted123";
        userModel.setPassword(plainPassword);

        // Stubbing de todas las dependencias del UseCase
        when(rolePersistencePort.getRoleById(2L)).thenReturn(roleModel);
        when(passwordEncoderPort.encode(plainPassword)).thenReturn(encryptedPassword);
        when(userPersistencePort.existsByEmail(anyString())).thenReturn(false);

        // Act
        userUseCase.saveOwner(userModel);

        // Assert
        assertEquals(encryptedPassword, userModel.getPassword()); // Verificamos que se encriptó
        assertNotNull(userModel.getRole());
        verify(userPersistencePort).saveUser(userModel);
    }

    @Test
    @DisplayName("Should throw exception when user is underage")
    void saveOwner_Underage_ThrowsException() {
        // Arrange
        when(rolePersistencePort.getRoleById(2L)).thenReturn(roleModel);
        userModel.setBirthDate(LocalDate.now().minusYears(15));

        // Act & Assert
        assertThrows(DomainException.class, () -> userUseCase.saveOwner(userModel));
    }

    @Test
    @DisplayName("Should throw exception when document is not numeric")
    void saveOwner_InvalidDocument_ThrowsException() {
        // Arrange
        when(rolePersistencePort.getRoleById(2L)).thenReturn(roleModel);
        userModel.setIdDocument("12345A78");

        // Act & Assert
        assertThrows(DomainException.class, () -> userUseCase.saveOwner(userModel));
    }

    @Test
    @DisplayName("Should throw exception when email already exists")
    void saveOwner_EmailExists_ThrowsException() {
        // Arrange
        when(rolePersistencePort.getRoleById(2L)).thenReturn(roleModel);
        when(userPersistencePort.existsByEmail(anyString())).thenReturn(true);

        // Act & Assert
        assertThrows(DomainException.class, () -> userUseCase.saveOwner(userModel));
    }
}