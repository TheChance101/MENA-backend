package net.thechance.events.identity

import net.thechance.events.MenaEvent
import net.thechance.events.identity.utils.Gender
import net.thechance.events.identity.utils.Status
import java.time.LocalDate

data class UserUpdatedEvent(
    val phoneNumber: String? = null,
    val password: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val username: String? = null,
    val imageUrl: String? = null,
    val birthDate: LocalDate? = null,
    val gender: Gender? = null,
    val status: Status? = null,
    val changedFields: List<ChangedField>
) : MenaEvent {
    enum class ChangedField(
        val key: String
    ){
        PHONE_NUMBER("phoneNumber"),
        PASSWORD("password"),
        FIRST_NAME("firstName"),
        LAST_NAME("lastName"),
        USERNAME("username"),
        IMAGE_URL("imageUrl"),
        BIRTH_DATE("birthDate"),
        GENDER("gender"),
        STATUS("status"),
    }
}
