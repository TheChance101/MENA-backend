package net.thechance.chat.repository

import net.thechance.chat.entity.MessageImages
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface MessageImagesRepository : JpaRepository<MessageImages, UUID>