package net.thechance.events.publisher

import net.thechance.events.MenaEvent

interface MenaEventPublisher {
    fun publish(event: MenaEvent)
}