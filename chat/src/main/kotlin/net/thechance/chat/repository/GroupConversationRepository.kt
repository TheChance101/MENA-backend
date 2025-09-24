package net.thechance.chat.repository

import net.thechance.chat.entity.Conversation
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface GroupConversation : JpaRepository<Conversation, UUID>