package org.example.cargotransporationmonitoring.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.CONFLICT)
class UserHasRoutesException(message: String) : AbstractCargoTransportationException(message)