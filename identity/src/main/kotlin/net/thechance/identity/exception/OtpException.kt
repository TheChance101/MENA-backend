package net.thechance.identity.exception

open class OtpException(message: String) : Exception(message)
class InvalidPhoneNumberException(message: String) : OtpException(message)
class FrequentOtpRequestException : OtpException("OTP request too frequent")
class InvalidOtpException : OtpException("Invalid OTP")
class OtpExpiredException : OtpException("OTP is expired")
class OtpAlreadyVerifiedException : OtpException("OTP is already verified")