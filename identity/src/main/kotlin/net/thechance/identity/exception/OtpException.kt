package net.thechance.identity.exception

open class OtpException(message: String) : Exception(message)
class InvalidPhoneNumberException(message: String) : OtpException(message)
class FrequentOtpRequestException : OtpException("Otp request too frequent")