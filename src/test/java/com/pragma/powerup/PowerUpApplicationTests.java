package com.pragma.powerup;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
class PowerUpApplicationTests {

    @Test
    void contextLoads() {
        // Este metodo verifica que el contexto de Spring se cargue correctamente.
        // Se deja vacío porque el simple hecho de que se ejecute sin lanzar excepciones
        // es la validación necesaria para confirmar la salud de la configuración inicial.
        assertDoesNotThrow(() -> {});
    }

}
