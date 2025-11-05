package net.thechance.chat.service.scheduler

import jakarta.transaction.Transactional
import net.thechance.chat.entity.CleanUpStatus
import net.thechance.chat.entity.DeletedChat
import net.thechance.chat.repository.DeletedChatRepository
import net.thechance.chat.repository.MessageRepository
import net.thechance.chat.service.AttachmentStorageService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.*

@Component
class ChatCleanUpScheduler(
    val deletedChatRepository: DeletedChatRepository,
    val messageRepository: MessageRepository,
    val attachmentStorageService: AttachmentStorageService,
){


    @Scheduled(initialDelay = ONE_HOUR, fixedDelay = ONE_HOUR)
    fun retryChatCleanUp(){
        try {
            val failingDeletedChats = deletedChatRepository.findAll()
            if(failingDeletedChats.isEmpty()) return

            failingDeletedChats.forEach {
                if(it.cleanUpStatus == CleanUpStatus.S3_DELETED_FAILED){
                    cleanUpImages(it.chatId.toString())
                }
                cleanUpChatData(it.chatId)
                //deletedChatRepository.save(DeletedChat(chatId = it.chatId, CleanUpStatus.DELETED))

            }
        }catch (e: Exception){
            println("failing of chat clean up scheduler: ${e.message}")
        }

    }

    private fun cleanUpImages(folderName: String){
        var attempts = 0
        while (attempts < MAX_ATTEMPTS){
            try {
                attachmentStorageService.deleteFolder(folderName)
                break
            }catch (e: Exception){
                attempts++
                Thread.sleep(1000L * attempts)
            }
        }
    }

    @Transactional
    private fun cleanUpChatData(chatId: UUID){
        var attempts = 0
        while (attempts < MAX_ATTEMPTS){
            try {
                messageRepository.deleteAllByChatId(chatId)
                //deletedChatRepository.deleteById(chatId)
                break
            }catch (e: Exception){
                attempts++
                Thread.sleep(1000L * attempts)
            }

        }
    }

    private companion object{
        const val MAX_ATTEMPTS = 3
        const val ONE_HOUR: Long = 3600000
    }
}