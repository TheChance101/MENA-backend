package net.thechance.faith.api.controller.exception

class AyahBookmarkNotFoundException(
    message: String = "Bookmark not found"
) : RuntimeException(message)

class CannotGetPrayerTimesException(
    message: String = "Cannot get prayer times"
) : RuntimeException(message)

class NearestMosqueNotFoundException(
    message: String = "Nearest mosque not found"
) : RuntimeException(message)
