package net.thechance.identity.service

import net.thechance.identity.security.config.RateLimitProperties
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

class IpRateLimitManagerServiceTest {

    private lateinit var rateLimitProperties: RateLimitProperties
    private lateinit var service: IpRateLimitManagerService

    @Before
    fun setUp() {
        rateLimitProperties = RateLimitProperties()
        service = IpRateLimitManagerService(rateLimitProperties)
    }

    @Test
    fun `isRequestAllowed should return true for an endpoint that is not configured`() {
        val isAllowed = service.isRequestAllowed(TEST_IP, UN_CONFIGURED_PATH)

        assertTrue(isAllowed)
    }

    @Test
    fun `isRequestAllowed should return true for initial requests within the short-term limit`() {
        val config = RateLimitProperties.EndpointRateLimitConfig(shortTermLimit = 3)
        rateLimitProperties.endpoints[PROTECTED_PATH] = config

        assertTrue(service.isRequestAllowed(TEST_IP, PROTECTED_PATH))
        assertTrue(service.isRequestAllowed(TEST_IP, PROTECTED_PATH))
        assertTrue(service.isRequestAllowed(TEST_IP, PROTECTED_PATH))
    }

    @Test
    fun `isRequestAllowed should return false when short-term limit is exceeded`() {
        val config = RateLimitProperties.EndpointRateLimitConfig(shortTermLimit = 2)
        rateLimitProperties.endpoints[PROTECTED_PATH] = config

        service.isRequestAllowed(TEST_IP, PROTECTED_PATH)
        service.isRequestAllowed(TEST_IP, PROTECTED_PATH)

        val isAllowed = service.isRequestAllowed(TEST_IP, PROTECTED_PATH)

        assertFalse(isAllowed)
    }

    @Test
    fun `isRequestAllowed should return false and block IP when long-term limit is exceeded`() {
        val config = RateLimitProperties.EndpointRateLimitConfig(
            shortTermLimit = 10,
            longTermLimit = 3,
            blockDurationSeconds = 300
        )
        rateLimitProperties.endpoints[PROTECTED_PATH] = config

        service.isRequestAllowed(TEST_IP, PROTECTED_PATH)
        service.isRequestAllowed(TEST_IP, PROTECTED_PATH)
        service.isRequestAllowed(TEST_IP, PROTECTED_PATH)

        val isAllowed = service.isRequestAllowed(TEST_IP, PROTECTED_PATH)

        assertFalse(isAllowed)

        val blockedIps = service.getBlockedIps()
        assertNotNull(blockedIps[TEST_IP])
        assertTrue(blockedIps[TEST_IP]!!.isAfter(Instant.now()))
    }

    @Test
    fun `isRequestAllowed should return false when an IP is actively blocked`() {
        val blockUntil = Instant.now().plusSeconds(60)
        service.getBlockedIps()[TEST_IP] = blockUntil

        val isAllowed = service.isRequestAllowed(TEST_IP, PROTECTED_PATH)

        assertFalse(isAllowed)
    }

    @Test
    fun `isRequestAllowed should unblock an IP after the block duration has expired`() {
        val expiredBlock = Instant.now().minusSeconds(60)
        service.getBlockedIps()[TEST_IP] = expiredBlock
        rateLimitProperties.endpoints[PROTECTED_PATH] = RateLimitProperties.EndpointRateLimitConfig()

        val isAllowed = service.isRequestAllowed(TEST_IP, PROTECTED_PATH)

        assertTrue(isAllowed)
        assertTrue(service.getBlockedIps().isEmpty())
    }

    companion object {
        private const val TEST_IP = "127.0.0.1"
        private const val PROTECTED_PATH = "/api/login"
        private const val UN_CONFIGURED_PATH = "/api/public-info"

        private fun IpRateLimitManagerService.getBlockedIps(): ConcurrentHashMap<String, Instant> {
            val field = this::class.java.getDeclaredField("blockedIps")
            field.isAccessible = true
            @Suppress("UNCHECKED_CAST")
            return field.get(this) as ConcurrentHashMap<String, Instant>
        }
    }
}

