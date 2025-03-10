package org.example.cargocommon.exception

import org.example.cargocommon.exception.AbstractCargoTransportationException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.CONFLICT)
class ResourceAlreadyExistException(message: String): AbstractCargoTransportationException(message)