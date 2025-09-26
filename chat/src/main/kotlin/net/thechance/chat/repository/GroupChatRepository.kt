package net.thechance.chat.repository

import net.thechance.chat.entity.GroupChat
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface GroupChatRepository : JpaRepository<GroupChat, UUID>