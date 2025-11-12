package net.thechance.chat.service.model

import org.springframework.data.domain.Pageable
import java.util.UUID

data class SearchContactsArgs(
    val query: String,
    val userId: UUID,
    val onlyMenaUsers: Boolean,
    val pageable: Pageable
)
