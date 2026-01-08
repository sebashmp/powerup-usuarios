package com.pragma.powerup.domain.util;

public final class UserErrorMessages {

    private UserErrorMessages() {
    }

    public static final String ONLY_ADMIN_CAN_CREATE_OWNER =
            "Only an admin can create an owner account.";

    public static final String ONLY_OWNER_CAN_CREATE_EMPLOYEE =
            "Only a restaurant owner can create an employee account.";

    public static final String USER_NOT_FOUND_PREFIX =
            "User not found with ID: ";

    public static final String ID_DOCUMENT_NUMERIC_ONLY =
            "ID Document must be purely numeric.";

    public static final String PHONE_MAX_LENGTH =
            "Phone number must not exceed 13 characters.";

    public static final String EMAIL_ALREADY_REGISTERED =
            "Email is already registered.";

    public static final String BIRTH_DATE_REQUIRED =
            "Birth date is required.";

    public static final String USER_MUST_BE_ADULT =
            "The user must be an adult (18+ years old).";
}
