package net.thechance.chat.service.exception

class NotFoundException(message: String) : RuntimeException(message)
class FetchWeatherException(message: String, cause: Exception? = null) : RuntimeException(message)