package net.thechance.faith.service

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import net.thechance.faith.exception.FailedToGetPrayerTimesException
import net.thechance.faith.entity.DayPrayerTimings
import net.thechance.faith.remote.PrayerRemoteClient
import net.thechance.faith.remote.dto.*
import net.thechance.faith.repository.PrayerRepository
import org.junit.Assert.assertThrows
import java.time.Instant
import java.time.LocalDate
import kotlin.test.Test

class PrayerServiceTest {
    private val remoteClient: PrayerRemoteClient = mockk()
    private val repository: PrayerRepository = mockk()
    private val service: PrayerService = PrayerService(prayerRepository = repository, prayerRemoteClient = remoteClient)

    @Test
    fun `getPrayerTimes should get cached data when its not expired`() {
        //Given
        every {
            repository.findByLatitudeAndLongitudeAndDateSortedByNearestLocation(
                latitude = LATITUDE,
                longitude = LONGITUDE,
                date = LOCAL_DATE
            )
        } returns listOf(cachedPrayerTimes)
        //When
        val result = service.getPrayerTimes(LATITUDE, LONGITUDE, DATE)
        //Then
        assertThat(result).isEqualTo(cachedPrayerTimes)
        verify(exactly = 0) {
            remoteClient.getPrayerTimes(any(), any(), any())
        }
    }

    @Test
    fun `getPrayerTimes should call remote client when cached data is expired`() {
        //Given
        every {
            repository.findByLatitudeAndLongitudeAndDateSortedByNearestLocation(
                latitude = LATITUDE,
                longitude = LONGITUDE,
                date = LOCAL_DATE
            )
        } returns listOf(cachedPrayerTimes.copy(savedIn = Instant.now().minusSeconds(86400 * 3)))
        every {
            repository.save(any<DayPrayerTimings>())
        } returns mockk(relaxed = true)
        every {
            remoteClient.getPrayerTimes(
                latitude = LATITUDE,
                longitude = LONGITUDE,
                date = DATE
            )
        } returns remotePrayerTimes
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
            repository.findByLatitudeAndLongitudeAndDateSortedByNearestLocation(
                latitude = LATITUDE,
                longitude = LONGITUDE,
                date = LOCAL_DATE
            )
        } returns listOf(cachedPrayerTimes.copy(savedIn = Instant.parse("2025-10-07T10:00:00Z")))
        every {
            remoteClient.getPrayerTimes(
                latitude = LATITUDE,
                longitude = LONGITUDE,
                date = DATE
            )
        } throws FailedToGetPrayerTimesException()
        //When //Then
        assertThrows(FailedToGetPrayerTimesException::class.java) {
            service.getPrayerTimes(LATITUDE, LONGITUDE, DATE)
        }
    }

    private companion object {
        const val LATITUDE = 30.0
        const val LONGITUDE = 31.0
        val LOCAL_DATE: LocalDate = LocalDate.now()
        val DATE = "${LOCAL_DATE.dayOfMonth}-${LOCAL_DATE.monthValue}-${LOCAL_DATE.year}"

        val cachedPrayerTimes = DayPrayerTimings(
            id = 1,
            latitude = 30.0444,
            longitude = 31.2357,
            savedIn = Instant.now(),
            date = LocalDate.of(2025, 10, 9),
            hijriDate = "17-04-1447",
            fajr = Instant.parse("2025-10-09T02:26:00Z"),
            sunrise = Instant.parse("2025-10-09T03:53:00Z"),
            dhuhr = Instant.parse("2025-10-09T09:42:00Z"),
            asr = Instant.parse("2025-10-09T13:02:00Z"),
            maghrib = Instant.parse("2025-10-09T15:30:00Z"),
            isha = Instant.parse("2025-10-09T16:47:00Z")
        )
        val remotePrayerTimes = PrayerTimingsRemoteDto(
            code = 1,
            status = "",
            data = PrayerDataRemoteDto(
                timings = TimingsRemoteDto(
                    fajr = "02:26",
                    sunrise = "03:53",
                    dhuhr = "09:42",
                    asr = "13:02",
                    maghrib = "15:30",
                    isha = "16:47"
                ),
                date = DateInfoRemoteDto(
                    timestamp = "1696828800",
                    hijri = HijriRemoteDto(
                        date = "17-04-1447",
                        format = "DD-MM-YYYY"
                    ),
                    gregorian = GregorianRemoteDto(
                        date = "09-10-2025",
                        format = "DD-MM-YYYY",
                        day = "1",
                        month = MonthGregorianRemoteDto(
                            number = 2
                        ),
                        year = "2025"
                    )
                ),
                meta = MetaRemoteDto(
                    timezone = "Africa/Cairo"
                )
            )
        )
    }
}
