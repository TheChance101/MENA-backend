package net.thechance.dukan.search.config

import net.thechance.dukan.service.DukanProductSearchService
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component


@Component
class ProductSearchDataInitializer(
    private val dukanProductSearchService: DukanProductSearchService
) {

    @Async
    @EventListener(ApplicationReadyEvent::class)
    fun init() {
        if (dukanProductSearchService.indexIsEmpty()) {
            dukanProductSearchService.seed()
        }
    }
}