package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.exception.DomainException;
import com.pragma.powerup.domain.model.RoleModel;
import com.pragma.powerup.domain.model.UserModel;
import com.pragma.powerup.domain.spi.IAuthenticationContextPort;
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

    @Mock
    private IAuthenticationContextPort authContextPort;

    @InjectMocks
    private UserUseCase userUseCase;

    private UserModel userModel;
    private RoleModel ownerRole;
    private RoleModel employeeRole;

    @BeforeEach
    void setUp() {
        userModel = new UserModel();
        userModel.setName("Sebastian");
        userModel.setLastName("Hidalgo");
        userModel.setIdDocument("123456789");
        userModel.setPhone("+573001234567");
        userModel.setBirthDate(LocalDate.now().minusYears(20));
        userModel.setEmail("test@mail.com");
        userModel.setPassword("plainPassword");

        ownerRole = new RoleModel(2L, "ROLE_PROPIETARIO", "Propietario");
        employeeRole = new RoleModel(3L, "ROLE_EMPLEADO", "Empleado");
    }

    @Test
    @DisplayName("Should save owner successfully when caller is Admin")
    void saveOwner_Success() {
        // Configuramos los mocks solo para lo que este flujo necesita
        when(authContextPort.getAuthenticatedUserRole()).thenReturn("ROLE_ADMIN");
        when(rolePersistencePort.getRoleById(2L)).thenReturn(ownerRole);
        when(passwordEncoderPort.encode(anyString())).thenReturn("encodedPass");
        when(userPersistencePort.existsByEmail(anyString())).thenReturn(false);

        userUseCase.saveOwner(userModel);

        verify(userPersistencePort).saveUser(userModel);
        assertEquals(2L, userModel.getRole().getId());
    }

    @Test
    @DisplayName("Should throw exception when user is underage")
    void saveOwner_Underage_ThrowsException() {
        // Arrange
        when(authContextPort.getAuthenticatedUserRole()).thenReturn("ROLE_ADMIN");
        when(rolePersistencePort.getRoleById(2L)).thenReturn(ownerRole);

        // ponemos fecha de nacimiento que hace al usuario menor
        userModel.setBirthDate(LocalDate.now().minusYears(16));

        assertThrows(DomainException.class, () -> userUseCase.saveOwner(userModel));
        verify(userPersistencePort, never()).saveUser(any());
    }

    @Test
    @DisplayName("Should throw exception when document is not numeric")
    void saveOwner_InvalidDocument_ThrowsException() {
        when(authContextPort.getAuthenticatedUserRole()).thenReturn("ROLE_ADMIN");
        userModel.setIdDocument("12345A78");

        // Act & Assert
        assertThrows(DomainException.class, () -> userUseCase.saveOwner(userModel));

        verify(userPersistencePort, never()).saveUser(any());
    }

    @Test
    @DisplayName("Should throw exception when email already exists")
    void saveOwner_EmailExists_ThrowsException() {
        when(authContextPort.getAuthenticatedUserRole()).thenReturn("ROLE_ADMIN");
        when(rolePersistencePort.getRoleById(2L)).thenReturn(ownerRole);
        when(passwordEncoderPort.encode(anyString())).thenReturn("encodedPass");
        when(userPersistencePort.existsByEmail(anyString())).thenReturn(true);

        assertThrows(DomainException.class, () -> userUseCase.saveOwner(userModel));

        verify(userPersistencePort, never()).saveUser(any());
    }

    @Test
    @DisplayName("Should throw exception when saveOwner is called by non-Admin")
    void saveOwner_NotAdmin_ThrowsException() {
        when(authContextPort.getAuthenticatedUserRole()).thenReturn("ROLE_PROPIETARIO");

        assertThrows(DomainException.class, () -> userUseCase.saveOwner(userModel));
        verify(userPersistencePort, never()).saveUser(any());
    }

    @Test
    @DisplayName("Should save employee successfully when caller is a Propietario")
    void saveEmployee_Success() {
        // Arrange
        when(authContextPort.getAuthenticatedUserRole()).thenReturn("ROLE_PROPIETARIO");
        when(rolePersistencePort.getRoleById(3L)).thenReturn(employeeRole);
        when(passwordEncoderPort.encode(anyString())).thenReturn("encodedPass");
        when(userPersistencePort.existsByEmail(anyString())).thenReturn(false);

        // Act
        userUseCase.saveEmployee(userModel);

        // Assert
        assertEquals(3L, userModel.getRole().getId());
        assertEquals("encodedPass", userModel.getPassword());
        verify(userPersistencePort).saveUser(userModel);
    }

    @Test
    @DisplayName("Should throw exception when an ADMIN tries to create an employee")
    void saveEmployee_CallerIsAdmin_ThrowsException() {
        // Arrange
        when(authContextPort.getAuthenticatedUserRole()).thenReturn("ROLE_ADMIN");

        // Act & Assert
        DomainException exception = assertThrows(DomainException.class,
                () -> userUseCase.saveEmployee(new UserModel()));

        assertEquals("Only a restaurant owner can create an employee account.", exception.getMessage());
        verify(userPersistencePort, never()).saveUser(any());
    }

    @Test
    @DisplayName("Should throw exception when creating employee with invalid phone")
    void saveEmployee_InvalidPhone_ThrowsException() {
        // Arrange
        UserModel invalidEmployee = new UserModel();
        invalidEmployee.setPhone("1234567890123456789"); // Muy largo
        invalidEmployee.setIdDocument("123");
        invalidEmployee.setPassword("pwd"); // asegurar que password no sea null

        when(authContextPort.getAuthenticatedUserRole()).thenReturn("ROLE_PROPIETARIO");
        when(rolePersistencePort.getRoleById(3L)).thenReturn(employeeRole);

        // Act & Assert
        assertThrows(DomainException.class, () -> userUseCase.saveEmployee(invalidEmployee));
    }

    @Test
    @DisplayName("Should throw exception when document is not numeric")
    void saveEmployee_InvalidDocument_ThrowsException() {
        userModel.setIdDocument("ABC123");
        when(authContextPort.getAuthenticatedUserRole()).thenReturn("ROLE_PROPIETARIO");
        when(rolePersistencePort.getRoleById(3L)).thenReturn(employeeRole);
        when(passwordEncoderPort.encode(anyString())).thenReturn("encodedPass");

        assertThrows(DomainException.class, () -> userUseCase.saveEmployee(userModel));
    }

    @Test
    @DisplayName("Should save client successfully (Public Registration)")
    void saveClient_Success() {
        // Arrange
        RoleModel clientRole = new RoleModel(4L, "ROLE_CLIENTE", "Cliente");
        when(rolePersistencePort.getRoleById(4L)).thenReturn(clientRole);
        when(passwordEncoderPort.encode(anyString())).thenReturn("encodedPass");
        when(userPersistencePort.existsByEmail(anyString())).thenReturn(false);

        // Act
        userUseCase.saveClient(userModel); // 'user' definido en el setUp

        // Assert
        assertEquals(4L, userModel.getRole().getId());
        verify(userPersistencePort).saveUser(userModel);
    }
}
