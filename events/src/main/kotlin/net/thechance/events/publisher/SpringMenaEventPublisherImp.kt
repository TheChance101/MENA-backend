package net.thechance.events.publisher

import net.thechance.events.MenaEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
internal class SpringMenaEventPublisherImp(
    private val applicationEventPublisher: ApplicationEventPublisher
): MenaEventPublisher {

    override fun publish(event: MenaEvent) {
        applicationEventPublisher.publishEvent(event)
    }
}