package net.thechance.chat.repository

import net.thechance.chat.entity.ChatParticipants
import net.thechance.chat.entity.ChatParticipantsId
import org.springframework.data.jpa.repository.JpaRepository

interface ChatParticipantsRepository : JpaRepository<ChatParticipants, ChatParticipantsId>