package com.example.food.dto.request.order;

import com.example.food.common.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UpdateStatusRequest {
    private Long orderId;
    private OrderStatus orderStatus;
}
