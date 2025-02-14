package org.example.cargoroute.util

object ErrorMessages {

    const val NEW_STATUS_SET_ALREADY_VISITED_POINTS_ERROR = "This route can't be marked as 'NEW' because there are also visited points"
    const val COMPLETED_STATUS_SET_NOT_VISITED_POINTS_YET_ERROR = "There also some points not visited yet"

    const val USER_NOT_FOUND_ERROR = "User with username: %s not found"
    const val USER_NOT_BELONG_ADMIN_ERROR = "User with id: %s not belong admin with id: %s"

    const val ROUTE_NOT_FOUND_ERROR = "Route with id: %s not found"
    const val ROUTE_NOT_NEW_DELETE_ERROR = "You can't delete route with id: %s because status is not 'NEW'"
    const val ROUTE_NOT_NEW_EDIT_ERROR = "You can't edit routes whith id: %s because status is not 'NEW'"

    const val JWT_SUBJECT_NOT_FOUND = "Subject (sub) not found in JWT"
}