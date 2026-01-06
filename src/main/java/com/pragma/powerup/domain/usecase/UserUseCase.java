package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.IUserServicePort;
import com.pragma.powerup.domain.exception.DomainException;
import com.pragma.powerup.domain.model.UserModel;
import com.pragma.powerup.domain.spi.IAuthenticationContextPort;
import com.pragma.powerup.domain.spi.IPasswordEncoderPort;
import com.pragma.powerup.domain.spi.IRolePersistencePort;
import com.pragma.powerup.domain.spi.IUserPersistencePort;
import com.pragma.powerup.domain.util.RoleConstants;

import java.time.LocalDate;
import java.time.Period;

public class UserUseCase implements IUserServicePort {

    private final IUserPersistencePort userPersistencePort;
    private final IRolePersistencePort rolePersistencePort;
    private final IPasswordEncoderPort passwordEncoderPort;
    private final IAuthenticationContextPort authContextPort;

    public UserUseCase(IUserPersistencePort userPersistencePort,
                       IRolePersistencePort rolePersistencePort,
                       IPasswordEncoderPort passwordEncoderPort,
                       IAuthenticationContextPort authContextPort) {
        this.userPersistencePort = userPersistencePort;
        this.rolePersistencePort = rolePersistencePort;
        this.passwordEncoderPort = passwordEncoderPort;
        this.authContextPort = authContextPort;
    }

    @Override
    public void saveOwner(UserModel userModel) {
        String callerRole = authContextPort.getAuthenticatedUserRole();
        if (!RoleConstants.ROLE_ADMIN.equals(callerRole)) {
            throw new DomainException("Only an admin can create an owner account.");
        }

        prepareAndSave(userModel, RoleConstants.ROLE_ID_PROPIETARIO, true);
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
        String callerRole = authContextPort.getAuthenticatedUserRole();
        if (!RoleConstants.ROLE_PROPIETARIO.equals(callerRole)) {
            throw new DomainException("Only a restaurant owner can create an employee account.");
        }

        prepareAndSave(userModel, RoleConstants.ROLE_ID_EMPLEADO, true);
    }

    @Override
    public void saveClient(UserModel userModel) {
        prepareAndSave(userModel, RoleConstants.ROLE_ID_CLIENTE, false);
    }

    /**
     * Helper to set role, encode password, run validations and persist the user.
     *
     * @param userModel     user to prepare and save
     * @param roleId        role id to fetch and assign
     * @param requireAdult  whether to validate adult age
     */
    private void prepareAndSave(UserModel userModel, Long roleId, boolean requireAdult) {
        userModel.setRole(rolePersistencePort.getRoleById(roleId));
        userModel.setPassword(passwordEncoderPort.encode(userModel.getPassword()));

        if (requireAdult) {
            validateAdult(userModel);
        }

        validateUserCommonRules(userModel);
        userPersistencePort.saveUser(userModel);
    }

    // Validación común entre todos los usuarios (ID numérico, teléfono, email único)
    private void validateUserCommonRules(UserModel user) {
        if (user.getIdDocument() == null || !user.getIdDocument().matches("\\d+")) {
            throw new DomainException("ID Document must be purely numeric.");
        }

        if (user.getPhone() == null || user.getPhone().length() > 13) {
            throw new DomainException("Phone number must not exceed 13 characters.");
        }

        if (userPersistencePort.existsByEmail(user.getEmail())) {
            throw new DomainException("Email is already registered.");
        }
    }

    // Para validar si es mayor de edad
    private void validateAdult(UserModel user) {
        if (user.getBirthDate() == null) {
            throw new DomainException("Birth date is required.");
        }

        if (Period.between(user.getBirthDate(), LocalDate.now()).getYears() < 18) {
            throw new DomainException("The user must be an adult (18+ years old).");
        }
    }
}
