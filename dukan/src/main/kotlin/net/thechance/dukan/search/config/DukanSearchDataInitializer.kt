package net.thechance.dukan.search.config

import net.thechance.dukan.service.DukanSearchService
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component


@Component
class DukanSearchDataInitializer(
    private val dukanSearchService: DukanSearchService
) {

    @Async
    @EventListener(ApplicationReadyEvent::class)
    fun init() {
        if (dukanSearchService.indexIsEmpty()) {
            dukanSearchService.seed()
        }
    }
}