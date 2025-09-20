package net.thechance.chat.repository

import net.thechance.chat.entity.ContactUser
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ContactUserRepository : JpaRepository<ContactUser, UUID>