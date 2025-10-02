package net.thechance.identity.service.sms

interface SmsService {
    /**
     * @param countryCode (e.g., "20" for Egypt of phone number "+20xxxxxxxxxx").
     * @param carrierPrefix (e.g., "11" of Etisalat for phone number "+2011xxxxxxxx")
     * @param phoneNumber The phone number to send the OTP to (e.g., "+201122334455")
     * @param message The OTP of 6 digits (e.g., "123456")
     */
    fun sendSms(countryCode: String, carrierPrefix: String, phoneNumber: String, message: String)
}