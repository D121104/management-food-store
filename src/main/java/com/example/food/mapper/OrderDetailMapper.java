package com.example.food.mapper;

import com.example.food.dto.response.order.OrderDetailResponse;
import com.example.food.entity.OrderDetailEntity;
import org.mapstruct.Mapper;

@Mapper
public interface OrderDetailMapper {
    OrderDetailResponse toOrderDetailResponse(OrderDetailEntity orderDetailEntity);

}
