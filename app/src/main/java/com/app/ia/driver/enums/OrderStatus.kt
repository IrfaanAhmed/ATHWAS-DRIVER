package com.app.ia.driver.enums

enum class OrderStatus(var orderStatus: Int) {
    ACKNOWLEDGED(0),
    PACKED(1),
    IN_TRANSIT(2),
    DELIVERED(3),
    CANCELLED(2)
}