package net.thechance.faith.service

import io.mockk.verify
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import net.thechance.faith.api.controller.exception.CannotGetPrayerTimesException
import net.thechance.faith.entity.DayPrayerTimings
import net.thechance.faith.remote.PrayerRemoteClient
import net.thechance.faith.repository.PrayerRepository
import org.junit.Assert.assertThrows
import java.time.Instant
import kotlin.test.Test

class PrayerServiceTest {
    private val remoteClient: PrayerRemoteClient = mockk()
    private val repository: PrayerRepository = mockk()
    private val service: PrayerService = PrayerService(prayerRepository = repository, prayerRemoteClient = remoteClient)

    @Test
    fun `getPrayerTimes should cached data when its not expired`() {
        //Given
        every {
            repository.findByLatitudeAndLongitudeAndGregorianDate(
                latitude = LATITUDE,
                longitude = LONGITUDE,
                date = DATE
            )
        } returns cachedDatePrayerTimes
        every {
            remoteClient.getPrayerTimes(any(), any(), any())
        }
        //When
        val result = service.getPrayerTimes(LATITUDE, LONGITUDE, DATE)
        //Then
        assertThat(result).isEqualTo(cachedDatePrayerTimes)
        verify(exactly = 0) {
            remoteClient.getPrayerTimes(any(), any(), any())
        }
    }

    @Test
    fun `getPrayerTimes should call remote client when cached data is expired`() {
        //Given
        every {
            repository.findByLatitudeAndLongitudeAndGregorianDate(
                latitude = LATITUDE,
                longitude = LONGITUDE,
                date = DATE
            )
        } returns cachedDatePrayerTimes.copy(savedIn = Instant.parse("2025-10-07T10:00:00Z"))
        every {
            repository.save(any<DayPrayerTimings>())
        } returns mockk(relaxed = true)
        every {
            remoteClient.getPrayerTimes(
                latitude = LATITUDE,
                longitude = LONGITUDE,
                date = DATE
            )
        } returns mockk(relaxed = true)
        //When
        service.getPrayerTimes(LATITUDE, LONGITUDE, DATE)
        //Then
        verify(exactly = 1) {
            remoteClient.getPrayerTimes(any(), any(), any())
        }
    }

    @Test
    fun `getPrayerTimes should throw CannotGetPrayerTimesException when fetch data fails`() {
        //Given
        every {
            repository.findByLatitudeAndLongitudeAndGregorianDate(
                latitude = LATITUDE,
                longitude = LONGITUDE,
                date = DATE
            )
        } returns cachedDatePrayerTimes.copy(savedIn = Instant.parse("2025-10-07T10:00:00Z"))
        every {
            remoteClient.getPrayerTimes(
                latitude = LATITUDE,
                longitude = LONGITUDE,
                date = DATE
            )
        } throws CannotGetPrayerTimesException()
        //When //Then
        assertThrows(CannotGetPrayerTimesException::class.java) {
            service.getPrayerTimes(LATITUDE, LONGITUDE, DATE)
        }
    }

    private companion object {
        const val LATITUDE = 30.0
        const val LONGITUDE = 31.0
        const val DATE = "2025-10-09"
        val cachedDatePrayerTimes = DayPrayerTimings(
            id = 1,
            latitude = 30.0444,
            longitude = 31.2357,
            savedIn = Instant.parse("2025-10-08T10:00:00Z"),
            gregorianDate = "2025-10-09",
            dateTimestamp = "2025-10-08T00:00:00Z",
            gregorianReadableDate = "08 October 2025",
            gregorianDay = "08",
            gregorianDayName = "Wednesday",
            gregorianMonth = 10,
            gregorianMonthName = "October",
            gregorianYear = "2025",
            hijriDate = "1447-04-15",
            hijriReadableDate = "15 Rabiʻ al-Thani 1447",
            hijriDay = "15",
            hijriDayName = "Al-Arba'a",
            hijriDayArabicName = "الأربعاء",
            hijriMonth = 4,
            hijriYear = "1447",
            hijriMonthName = "Rabiʻ al-Thani",
            hijriMonthArabicName = "ربيع الآخر",
            fajr = "04:28",
            sunrise = "05:53",
            dhuhr = "11:41",
            asr = "15:02",
            maghrib = "17:29",
            isha = "18:49"
        )
    }
}
