package net.thechance.chat.repository

import net.thechance.chat.entity.DeletedChat
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface DeletedChatRepository : JpaRepository<DeletedChat, UUID>
