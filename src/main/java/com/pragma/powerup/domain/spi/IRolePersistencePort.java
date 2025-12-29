package com.pragma.powerup.domain.spi;

import com.pragma.powerup.domain.model.RoleModel;

public interface IRolePersistencePort {
    RoleModel getRoleById(Long id);
}