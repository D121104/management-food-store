package com.example.food.dto.request.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class OrderRequest {
    private Long addressId;
    private List<OrderDetailRequest> orderDetails;
    private Long deliveryPrice;
}
