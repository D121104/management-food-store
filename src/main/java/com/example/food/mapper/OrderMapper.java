package com.example.food.mapper;

import com.example.food.dto.response.order.OrderResponse;
import com.example.food.entity.OrderEntity;
import org.mapstruct.Mapper;

@Mapper
public interface OrderMapper {
    OrderResponse toOrderResponse(OrderEntity orderEntity);
}
