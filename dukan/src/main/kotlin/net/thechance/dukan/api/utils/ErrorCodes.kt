package net.thechance.dukan.api.utils

internal object ErrorCodes {
    const val DUKAN_CREATION_FAILED = 1101
    const val DUKAN_NOT_FOUND = 1102
    const val INVALID_IMAGE_FORMAT = 1201
    const val IMAGE_UPLOAD_FAILED = 1202

    const val IMAGE_DELETION_FAILED = 1203
    const val PRODUCT_NOT_FOUND = 1301
    const val DUKAN_PRODUCT_CREATION_FAILED = 1302
    const val PRODUCT_NAME_ALREADY_TAKEN = 1303

    const val PRODUCT_UPDATE_FAILED = 1304
    const val PRODUCT_OUT_OF_STOCK = 1305
    const val  INVALID_DISCOUNT = 1305
    const val SHELF_DELETION_NOT_ALLOWED = 1401
    const val SHELF_NOT_FOUND = 1402
    const val SHELF_NAME_ALREADY_TAKEN = 1403
    const val SHELF_NAME_NOT_CHANGED = 1404
    const val CART_NOT_FOUND = 1501
    const val PRODUCT_NOT_IN_CART = 1502
    const val PRODUCT_ALREADY_IN_CART = 1503
    const val FORBIDDEN_ORDER_ACCESS = 1601
    const val ORDER_NOT_FOUND = 1602
    const val USER_NOT_FOUND = 404
}