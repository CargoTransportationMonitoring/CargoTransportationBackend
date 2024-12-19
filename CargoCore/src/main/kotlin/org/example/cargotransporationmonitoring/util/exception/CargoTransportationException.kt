package org.example.cargotransporationmonitoring.util.exception

import org.example.cargotransporationmonitoring.util.locale.MessageUtil

abstract class CargoTransportationException(messageKey: String, vararg args: String): Exception(MessageUtil.getLocalizedMessage(messageKey, *args))