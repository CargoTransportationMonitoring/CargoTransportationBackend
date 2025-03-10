package org.example.cargotransporationmonitoring.exception

import org.example.cargocommon.exception.AbstractCargoTransportationException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.BAD_REQUEST)
class LinkCodeInvalidException(message: String): AbstractCargoTransportationException(message)