package net.thechance.events

import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
internal class SpringMenaEventPublisherImp(
    private val applicationEventPublisher: ApplicationEventPublisher
): MenaEventPublisher {

    override fun publish(event: Any) {
        applicationEventPublisher.publishEvent(event)
    }
}