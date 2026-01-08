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

import static com.pragma.powerup.domain.util.UserErrorMessages.*;

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
            throw new DomainException(ONLY_ADMIN_CAN_CREATE_OWNER);
        }

        prepareAndSave(userModel, RoleConstants.ROLE_ID_PROPIETARIO, true);
    }

    @Override
    public UserModel getUser(Long id) {
        UserModel user = userPersistencePort.getUser(id);
        if (user == null) {
            throw new DomainException(USER_NOT_FOUND_PREFIX + id);
        }
        return user;
    }

    @Override
    public void saveEmployee(UserModel userModel) {
        String callerRole = authContextPort.getAuthenticatedUserRole();
        if (!RoleConstants.ROLE_PROPIETARIO.equals(callerRole)) {
            throw new DomainException(ONLY_OWNER_CAN_CREATE_EMPLOYEE);
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
            throw new DomainException(ID_DOCUMENT_NUMERIC_ONLY);
        }

        if (user.getPhone() == null || user.getPhone().length() > 13) {
            throw new DomainException(PHONE_MAX_LENGTH);
        }

        if (userPersistencePort.existsByEmail(user.getEmail())) {
            throw new DomainException(EMAIL_ALREADY_REGISTERED);
        }
    }

    // Para validar si es mayor de edad
    private void validateAdult(UserModel user) {
        if (user.getBirthDate() == null) {
            throw new DomainException(BIRTH_DATE_REQUIRED);
        }

        if (Period.between(user.getBirthDate(), LocalDate.now()).getYears() < 18) {
            throw new DomainException(USER_MUST_BE_ADULT);
        }
    }
}
