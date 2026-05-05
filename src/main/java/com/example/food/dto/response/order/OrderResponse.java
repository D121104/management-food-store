package com.example.food.dto.response.order;

import com.example.food.common.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class OrderResponse {
    private Long id;
    private OrderStatus status;
    private Long totalPrice;
    private Double latitude;
    private Double longitude;
    private String address;
    private Long userId;
    private LocalDateTime createdAt;
    private Long deliveryPrice;

    private List<OrderDetailResponse> orderDetails;
}
