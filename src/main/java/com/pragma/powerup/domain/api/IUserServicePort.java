package com.pragma.powerup.domain.api;

import com.pragma.powerup.domain.model.UserModel;

public interface IUserServicePort {
    void saveOwner(UserModel userModel);
    UserModel getUser(Long id);
    void saveEmployee(UserModel userModel);
    void saveClient(UserModel userModel);
}
