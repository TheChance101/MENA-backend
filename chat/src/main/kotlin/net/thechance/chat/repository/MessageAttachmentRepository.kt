package net.thechance.chat.repository

import net.thechance.chat.entity.MessageAttachment
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface MessageAttachmentRepository : JpaRepository<MessageAttachment, UUID>