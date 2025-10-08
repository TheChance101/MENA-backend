package net.thechance.identity.exception

abstract class AddressException(message: String): Exception(message)
class AddressNotFoundException(message: String = "Address Not Found"): AddressException(message)
class AddressCanNotBeDeletedException(message: String = "Address Can Not Be Deleted"): AddressException(message)
class AddressNotAddedException(message: String = "Address Not Added"): AddressException(message)
class AddressNotUpdatedException(message: String = "Address Not Updated"): AddressException(message)
class AddressNotDeletedException(message: String = "Address Not Deleted"): AddressException(message)