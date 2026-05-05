package com.example.food.common;

public enum OrderStatus {
    PENDING,          // Chờ xác nhận
    CONFIRMED,        // Đã xác nhận
    WAITING_DELIVERY, // Chờ giao hàng
    DELIVERING,       // Đang giao
    DELIVERED,        // Đã giao
    CANCELLED ,       //Đã hủy
    ACTIVE,
    COMPLETED;


}
