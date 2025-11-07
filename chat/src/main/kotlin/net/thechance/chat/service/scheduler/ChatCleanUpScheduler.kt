package net.thechance.chat.service.scheduler

import jakarta.transaction.Transactional
import net.thechance.chat.entity.CleanUpStatus
import net.thechance.chat.entity.DeletedChat
import net.thechance.chat.repository.ChatRepository
import net.thechance.chat.repository.DeletedChatRepository
import net.thechance.chat.repository.MessageReactionRepository
import net.thechance.chat.repository.MessageRepository
import net.thechance.chat.service.AttachmentStorageService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class ChatCleanUpScheduler(
    val deletedChatRepository: DeletedChatRepository,
    val messageRepository: MessageRepository,
    val messageReactionRepository: MessageReactionRepository,
    val chatRepository: ChatRepository,
    val attachmentStorageService: AttachmentStorageService,
) {


    @Scheduled(initialDelay = ONE_HOUR, fixedDelay = ONE_HOUR)
    @Transactional
    fun retryChatCleanUp() {
        try {
            val failingDeletedChats = deletedChatRepository.findAllByCleanUpStatusNot(CleanUpStatus.DELETED)
            if (failingDeletedChats.isEmpty()) return

            failingDeletedChats.forEach { deletedChat ->
                when (deletedChat.cleanUpStatus) {
                    CleanUpStatus.PENDING,
                    CleanUpStatus.MEDIA_DELETED_FAILED -> {
                        deleteAllData(deletedChat)
                    }

                    CleanUpStatus.DATA_CLEANUP_FAILED -> {
                        val dbSuccess = cleanUpChatData(deletedChat.chatId, deletedChat)
                        if (dbSuccess) {
                            deletedChat.cleanUpStatus = CleanUpStatus.DELETED
                            deletedChatRepository.save(deletedChat)
                        }
                    }

                    CleanUpStatus.DELETED -> Unit
                }
            }
        } catch (e: Exception) {
            println("failing of chat clean up scheduler: ${e.message}")
        }

    }

    private fun deleteAllData(deletedChat: DeletedChat){
        val s3Success = cleanUpImages(deletedChat.chatId.toString(), deletedChat)
        if (s3Success) {
            val dbSuccess = cleanUpChatData(deletedChat.chatId, deletedChat)
            if (dbSuccess) {
                deletedChat.cleanUpStatus = CleanUpStatus.DELETED
                deletedChatRepository.save(deletedChat)
            } else {
                deletedChat.cleanUpStatus = CleanUpStatus.DATA_CLEANUP_FAILED
                deletedChatRepository.save(deletedChat)
            }
        } else {
            deletedChat.cleanUpStatus = CleanUpStatus.MEDIA_DELETED_FAILED
            deletedChatRepository.save(deletedChat)
        }
    }

    private fun cleanUpImages(folderName: String, deletedChat: DeletedChat): Boolean {
        var attempts = 0
        while (attempts < MAX_ATTEMPTS) {
            try {
                attachmentStorageService.deleteFolder(folderName)
                return true
            } catch (e: Exception) {
                attempts++
                Thread.sleep(1000L * attempts)
            }
        }
        return false
    }

    @Transactional
    private fun cleanUpChatData(chatId: UUID, deletedChat: DeletedChat): Boolean {
        var attempts = 0
        while (attempts < MAX_ATTEMPTS) {
            try {
                messageReactionRepository.deleteAllByChatId(chatId)
                messageRepository.deleteAllByChatId(chatId)
                chatRepository.deleteChatUsersByChatId(chatId)
                chatRepository.deleteChatById(chatId)
                return true
            } catch (e: Exception) {
                attempts++
                Thread.sleep(1000L * attempts)
                println("=====> clean up data exception: $e")

            }

        }
        return false
    }

    private companion object {
        const val MAX_ATTEMPTS = 3
        const val ONE_HOUR: Long = 20000
    }
}