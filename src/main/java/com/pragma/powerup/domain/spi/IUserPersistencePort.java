package com.pragma.powerup.domain.spi;

import com.pragma.powerup.domain.model.UserModel;

public interface IUserPersistencePort {
    void saveUser(UserModel userModel);
    boolean existsByEmail(String email);
    boolean existsByIdDocument(String idDocument);
    UserModel getUser(Long id);
    UserModel findByEmail(String email);
}