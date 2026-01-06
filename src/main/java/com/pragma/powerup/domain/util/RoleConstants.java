package com.pragma.powerup.domain.util;

public final class RoleConstants {

    private RoleConstants() {}

    // Role names
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_PROPIETARIO = "ROLE_PROPIETARIO";
    public static final String ROLE_EMPLEADO = "ROLE_EMPLEADO";
    public static final String ROLE_CLIENTE = "ROLE_CLIENTE";

    // Role IDs (coincidir con script SQL / BD)
    public static final Long ROLE_ID_PROPIETARIO = 2L;
    public static final Long ROLE_ID_EMPLEADO = 3L;
    public static final Long ROLE_ID_CLIENTE = 4L;
}
