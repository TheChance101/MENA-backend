package net.thechance.faith.remote.mapper

import com.google.common.truth.Truth.assertThat
import java.time.Instant
import kotlin.test.Test

class StringHoursAndMinutesToInstantMapperTest : StringHoursAndMinutesToInstantMapper {

    @Test
    fun `toInstant should map valid time string to correct Instant`() {
        // Given
        val time = "05:25"
        // When
        val result = time.toInstant(startOfDay = START_OF_DAY_INSTANT)
        // Then
        val expected = START_OF_DAY_INSTANT
            .plusSeconds(5 * 3600) // 5 hours
            .plusSeconds(25 * 60) // 25 minutes
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `toInstant should return startOfDay when time is null`() {
        // When
        val result = null.toInstant(startOfDay = START_OF_DAY_INSTANT)
        // Then
        assertThat(result).isEqualTo(START_OF_DAY_INSTANT)
    }

    @Test
    fun `toInstant should return startOfDay when format invalid`() {
        // Given
        val invalidTime = "abc"
        // When
        val result = invalidTime.toInstant(startOfDay = START_OF_DAY_INSTANT)
        // Then
        assertThat(result).isEqualTo(START_OF_DAY_INSTANT)
    }

    private companion object {
        val START_OF_DAY_INSTANT: Instant = Instant.parse("2025-10-10T00:00:00Z")// 2025-10-10 00:00:00 UTC
    }
}
