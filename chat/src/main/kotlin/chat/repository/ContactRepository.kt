package net.thechance.chat.repository

import net.thechance.chat.entity.Contact
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ContactRepository  : JpaRepository<Contact, UUID>