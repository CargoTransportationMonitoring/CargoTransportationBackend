package org.example.cargotransporationmonitoring.util

object ErrorMessages {
    const val USER_ALREADY_LINKED_ERROR = "User withId: %s is already linked to an admin"
    const val USER_ALREADY_EXIST_ERROR = "User with Email: %s or Username: %s is already exist"
    const val USER_HAS_ROUTES_ERROR = "User withId: %s has unprocessed routes yet"
    const val USER_NOT_FOUND_ERROR = "There is no user with such username: %s"

    const val LINK_CODE_CONTAINS_NOT_VALID_USERNAME = "The linked code is not valid for this user: %s"

    const val JWT_TOKEN_NOT_FOUND = "JWT token not found in SecurityContext"
    const val SUBJECT_NOT_FOUND_IN_JWT = "Subject (sub) not found in JWT"
}