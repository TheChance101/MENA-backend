package net.thechance.faith.api.controller.exception

class AyahBookmarkNotFoundException(
    message: String = "Bookmark not found"
) : RuntimeException(message)
