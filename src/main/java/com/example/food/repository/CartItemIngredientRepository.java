package com.example.food.repository;

import com.example.food.entity.CartItemIngredientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemIngredientRepository extends JpaRepository<CartItemIngredientEntity, Long> {
    List<CartItemIngredientEntity> findAllByCartItemId(Long cartItemId);
    void deleteAllByCartItemId(Long cartItemId);
}
