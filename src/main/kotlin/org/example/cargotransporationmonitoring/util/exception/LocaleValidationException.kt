package org.example.cargotransporationmonitoring.util.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.BAD_REQUEST)
class LocaleValidationException(messageKey: String, vararg args: String): CargoTransportationException(messageKey, *args)