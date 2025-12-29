package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.IUserServicePort;
import com.pragma.powerup.domain.exception.DomainException; // Crea esta clase
import com.pragma.powerup.domain.model.UserModel;
import com.pragma.powerup.domain.spi.IPasswordEncoderPort;
import com.pragma.powerup.domain.spi.IRolePersistencePort;
import com.pragma.powerup.domain.spi.IUserPersistencePort;
import java.time.LocalDate;
import java.time.Period;

public class UserUseCase implements IUserServicePort {

    private final IUserPersistencePort userPersistencePort;
    private final IRolePersistencePort rolePersistencePort;
    private final IPasswordEncoderPort passwordEncoderPort;

    public UserUseCase(IUserPersistencePort userPersistencePort, IRolePersistencePort rolePersistencePort, IPasswordEncoderPort passwordEncoderPort) {
        this.userPersistencePort = userPersistencePort;
        this.rolePersistencePort = rolePersistencePort;
        this.passwordEncoderPort = passwordEncoderPort;
    }

    @Override
    public void saveOwner(UserModel userModel) {
        userModel.setPassword(passwordEncoderPort.encode(userModel.getPassword()));
        userModel.setRole(rolePersistencePort.getRoleById(2L));
        validateOwnerRules(userModel);
        userPersistencePort.saveUser(userModel);
    }

    private void validateOwnerRules(UserModel user) {
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
}
