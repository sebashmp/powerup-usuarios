package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.exception.DomainException;
import com.pragma.powerup.domain.model.RoleModel;
import com.pragma.powerup.domain.model.UserModel;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserUseCaseTest {

    @Mock
    private IUserPersistencePort userPersistencePort;

    @Mock
    private IRolePersistencePort rolePersistencePort;

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
        when(rolePersistencePort.getRoleById(2L)).thenReturn(roleModel); // Configurar el mock del rol
        when(userPersistencePort.existsByEmail(anyString())).thenReturn(false);

        // Act
        userUseCase.saveOwner(userModel);

        // Assert
        assertNotNull(userModel.getRole()); // Verificar que el UseCase asignó el rol
        assertEquals(2L, userModel.getRole().getId());
        verify(userPersistencePort, times(1)).saveUser(userModel);
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