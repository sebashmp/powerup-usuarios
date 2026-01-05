package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.IUserServicePort;
import com.pragma.powerup.domain.exception.DomainException; // Crea esta clase
import com.pragma.powerup.domain.model.UserModel;
import com.pragma.powerup.domain.spi.IAuthenticationContextPort;
import com.pragma.powerup.domain.spi.IPasswordEncoderPort;
import com.pragma.powerup.domain.spi.IRolePersistencePort;
import com.pragma.powerup.domain.spi.IUserPersistencePort;
import java.time.LocalDate;
import java.time.Period;

public class UserUseCase implements IUserServicePort {

    private final IUserPersistencePort userPersistencePort;
    private final IRolePersistencePort rolePersistencePort;
    private final IPasswordEncoderPort passwordEncoderPort;
    private final IAuthenticationContextPort authContextPort;

    public UserUseCase(IUserPersistencePort userPersistencePort, IRolePersistencePort rolePersistencePort, IPasswordEncoderPort passwordEncoderPort, IAuthenticationContextPort  authContextPort) {
        this.userPersistencePort = userPersistencePort;
        this.rolePersistencePort = rolePersistencePort;
        this.passwordEncoderPort = passwordEncoderPort;
        this.authContextPort = authContextPort;
    }

    @Override
    public void saveOwner(UserModel userModel) {
        // 1. REGLA DE NEGOCIO: Validar que el que llama sea un Propietario
        String callerRole = authContextPort.getAuthenticatedUserRole();
        if (!"ROLE_PROPIETARIO".equals(callerRole)) {
            throw new DomainException("Only a restaurant owner can create an employee account.");
        }

        // 2. REGLA DE NEGOCIO: Asignar el Rol de Empleado (ID 3)
        userModel.setRole(rolePersistencePort.getRoleById(3L));

        // 3. REGLA DE NEGOCIO: Encriptar clave antes de guardar
        userModel.setPassword(passwordEncoderPort.encode(userModel.getPassword()));

        // 4. Validaciones comunes (Email único, DNI numérico, etc.)
        validateUserCommonRules(userModel);

        userPersistencePort.saveUser(userModel);
    }

    private void validateUserCommonRules(UserModel user) {
        if (Period.between(user.getBirthDate(), LocalDate.now()).getYears() < 18) {
            throw new DomainException("The user must be an adult (18+ years old).");
        }
        if (!user.getIdDocument().matches("\\d+")) {
            throw new DomainException("ID Document must be purely numeric.");
        }
        if (user.getPhone().length() > 13) {
            throw new DomainException("Phone number must not exceed 13 characters.");
        }
        if (userPersistencePort.existsByEmail(user.getEmail())) {
            throw new DomainException("Email is already registered.");
        }
    }

    @Override
    public UserModel getUser(Long id) {
        UserModel user = userPersistencePort.getUser(id);
        if (user == null) {
            throw new DomainException("User not found with ID: " + id);
        }
        return user;
    }

    @Override
    public void saveEmployee(UserModel userModel) {
        // 1. REGLA DE NEGOCIO: Validar que el que llama sea un Propietario
        String callerRole = authContextPort.getAuthenticatedUserRole();
        if (!"ROLE_PROPIETARIO".equals(callerRole)) {
            throw new DomainException("Only a restaurant owner can create an employee account.");
        }

        // 2. REGLA DE NEGOCIO: Asignar el Rol de Empleado (ID 3 según nuestro script SQL)
        userModel.setRole(rolePersistencePort.getRoleById(3L));

        // 3. REGLA DE NEGOCIO: Encriptar clave (Encapsulación de seguridad)
        userModel.setPassword(passwordEncoderPort.encode(userModel.getPassword()));

        // 4. Validaciones de campos (Reutilizamos la lógica de la HU-1)
        validateUserCommonRules(userModel);

        userPersistencePort.saveUser(userModel);
    }
}
