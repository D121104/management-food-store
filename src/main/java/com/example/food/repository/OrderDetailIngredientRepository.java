package com.example.food.repository;

import com.example.food.entity.OrderDetailIngredientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailIngredientRepository extends JpaRepository<OrderDetailIngredientEntity, Long> {
    List<OrderDetailIngredientEntity> findAllByOrderDetailId(Long orderDetailId);

}
