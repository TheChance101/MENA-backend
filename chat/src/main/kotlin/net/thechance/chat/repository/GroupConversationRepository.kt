package net.thechance.chat.repository

import net.thechance.chat.entity.Chat
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface GroupChat : JpaRepository<Chat, UUID>