package com.pragma.powerup.infrastructure.out.jpa.adapter;

import com.pragma.powerup.domain.model.UserModel;
import com.pragma.powerup.domain.spi.IUserPersistencePort;
import com.pragma.powerup.infrastructure.out.jpa.mapper.IUserEntityMapper;
import com.pragma.powerup.infrastructure.out.jpa.repository.IUserRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserJpaAdapter implements IUserPersistencePort {

    private final IUserRepository userRepository;
    private final IUserEntityMapper userEntityMapper;

    @Override
    public void saveUser(UserModel userModel) {
        // Mapeamos de Modelo de Dominio a Entidad JPA y guardamos
        userRepository.save(userEntityMapper.toEntity(userModel));
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByIdDocument(String idDocument) {
        return userRepository.existsByIdDocument(idDocument);
    }

    @Override
    public UserModel getUser(Long id) {
        return userRepository.findById(id)
                .map(userEntityMapper::toModel)
                .orElse(null);
    }

    @Override
    public UserModel findByEmail(String email) {
        // Trim para eliminar espacios accidentales antes de enviar al repositorio
        return userRepository.findByEmail(email.trim())
                .map(userEntityMapper::toModel)
                .orElse(null);
    }
}