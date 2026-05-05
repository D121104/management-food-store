package com.example.food.repository;

import com.example.food.dto.response.order.OrderDetailResponse;
import com.example.food.entity.OrderDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetailEntity, Long> {
    boolean existsByOrderIdAndFoodId(Long orderId, Long foodId);
    List<OrderDetailEntity> findAllByOrderId(Long orderId);
}
