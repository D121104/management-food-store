package com.example.food.repository;

import com.example.food.entity.CartItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItemEntity,Long> {
    List<CartItemEntity> findAllByUserId(Long userId);
    Optional<CartItemEntity> findByUserIdAndFoodId(Long userId, Long foodId);

}
