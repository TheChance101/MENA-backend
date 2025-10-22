package net.thechance.faith.exception

class AyahBookmarkNotFoundException(
    message: String = "Bookmark not found"
) : RuntimeException(message)

class FailedToGetPrayerTimesException(
    message: String = "failed to get prayer times"
) : RuntimeException(message)
