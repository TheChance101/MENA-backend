package net.thechance.identity.service

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import net.thechance.identity.security.config.RateLimitProperties
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong

@Service
class RateLimitManagerService(
    private val rateLimitProperties: RateLimitProperties
) {
    private val blockedIps: ConcurrentHashMap<String, Instant> = ConcurrentHashMap()
    private val shortTermCaches: ConcurrentHashMap<String, Cache<String, AtomicLong>> = ConcurrentHashMap()
    private val longTermCaches: ConcurrentHashMap<String, Cache<String, AtomicLong>> = ConcurrentHashMap()

    fun isRequestAllowed(ip: String, requestPath: String): Boolean {
        val blockUntil = blockedIps[ip]
        if (blockUntil != null && blockUntil.isAfter(Instant.now())) {
            return false
        } else if (blockUntil != null) {
            blockedIps.remove(ip)
        }

        val config = rateLimitProperties.endpoints[requestPath] ?: return true

        val shortTermEndpointCache = shortTermCaches.getOrCreateCache(requestPath, config.shortTermWindowSeconds)
        val longTermEndpointCache = longTermCaches.getOrCreateCache(requestPath, config.longTermWindowSeconds)

        val currentShortTermAttempts = shortTermEndpointCache.get(ip) { AtomicLong(0) }!!.incrementAndGet()
        val currentLongTermAttempts = longTermEndpointCache.get(ip) { AtomicLong(0) }!!.incrementAndGet()

        if (currentLongTermAttempts > config.longTermLimit) {
            if (currentLongTermAttempts >= config.longTermLimit + 1) {
                blockedIps[ip] = Instant.now().plusSeconds(config.blockDurationSeconds)
                return false
            }
            return false
        }

        if (currentShortTermAttempts > config.shortTermLimit) {
            return false
        }

        return true
    }

    private fun ConcurrentHashMap<String, Cache<String, AtomicLong>>.getOrCreateCache(
        endpointPath: String,
        windowDurationInSeconds: Long
    ): Cache<String, AtomicLong> {
        return computeIfAbsent(endpointPath) {
            Caffeine.newBuilder().expireAfterWrite(windowDurationInSeconds, TimeUnit.SECONDS)
                .maximumSize(rateLimitProperties.globalMaxIpsToTrack).build()
        }
    }
}