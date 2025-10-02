package net.thechance.trends.exception


open class TrendExceptions(messages: String): Exception(messages)

class ReelNotFoundException: TrendExceptions("Reel not found")

class TrendUserNotFoundException: TrendExceptions("User not found")

class TrendCategoryNotFoundException: TrendExceptions("Category not found")

class InvalidTrendInputException: TrendExceptions("Invalid input")

class InvalidVideoException: TrendExceptions("Invalid video")

class VideoUploadFailedException: TrendExceptions("Video upload failed")

class VideoDeleteFailedException: TrendExceptions("Failed to delete video from storage")