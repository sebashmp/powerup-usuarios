package com.pragma.powerup;

import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest(properties = "spring.profiles.active=test")
class PowerUpApplicationTests {

    @BeforeAll
    static void setup() {
        // Cargamos las variables de entorno para que el contexto de Spring en el test las reconozca
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
    }

    @Test
    void contextLoads() {
        // Este metodo verifica que el contexto de Spring se cargue correctamente.
        // Se deja vacío porque el simple hecho de que se ejecute sin lanzar excepciones
        // es la validación necesaria para confirmar la salud de la configuración inicial.
        assertDoesNotThrow(() -> {});
    }

}
