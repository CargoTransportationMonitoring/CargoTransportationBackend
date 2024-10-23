package org.example.cargotransporationmonitoring.util.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.NOT_FOUND)
class EntityNotFoundException(messageKey: String, vararg args: String): CargoTransportationException(messageKey, *args)
