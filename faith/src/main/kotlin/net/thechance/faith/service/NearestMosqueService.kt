package net.thechance.faith.service

import jakarta.transaction.Transactional
import net.thechance.faith.api.controller.exception.NearestMosqueNotFoundException
import net.thechance.faith.entity.Mosque
import net.thechance.faith.repository.NearestMosqueRepository
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.lang.Exception
import java.util.*

@Service
class NearestMosqueService(
    private val mosqueRepository: NearestMosqueRepository,
    private val imageStorageService: FaithImageStorageService,

    ) {

    fun createNearestMosque(mosque: Mosque): Mosque {
        return mosqueRepository.save(mosque)
    }

    @Transactional
    fun uploadMosqueImage(mosqueId: UUID, files: List<MultipartFile>): List<String> {
        val mosque = mosqueRepository.findById(mosqueId)
            .orElseThrow {
                NearestMosqueNotFoundException()
            }
        val imageUrls = mutableListOf<String>()
        try {
            files.forEach { file ->
                val imageUrl = imageStorageService.uploadImage(
                    file = file,
                    fileName = "${mosque.name}-${file.originalFilename}",
                    folderName = MOSQUE_FOLDER_NAME
                )
                imageUrls.add(imageUrl)
            }
        } catch (e: Exception) {
            mosqueRepository.delete(mosque)
            throw e
        }
        mosqueRepository.save(mosque.copy(imageUrls = imageUrls))
        return imageUrls
    }

    companion object {
        private const val MOSQUE_FOLDER_NAME = "mosque"
    }
}