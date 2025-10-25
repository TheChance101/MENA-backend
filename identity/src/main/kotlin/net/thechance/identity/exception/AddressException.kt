package net.thechance.identity.exception

abstract class AddressException(message: String) : Exception(message)
class AddressNotFoundException(message: String = "Address Not Found") : AddressException(message)
class AddressCanNotBeDeletedException(message: String = "Address Can Not Be Deleted") : AddressException(message)
class AtLeastAddressValueNeededException(message: String = "At least one value needed to update") :
    AddressException(message)

class AddressCanNotBeUpdatedException(message: String = "Address Can Not Be Updated") : AddressException(message)
class DataNotValidException(message: String = "Data not valid") : AddressException(message)
