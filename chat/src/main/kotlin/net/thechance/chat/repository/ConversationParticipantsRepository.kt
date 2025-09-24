package net.thechance.chat.repository

import net.thechance.chat.entity.ConversationParticipants
import net.thechance.chat.entity.ConversationParticipantsId
import org.springframework.data.jpa.repository.JpaRepository

interface ConversationParticipantsRepository : JpaRepository<ConversationParticipants, ConversationParticipantsId>