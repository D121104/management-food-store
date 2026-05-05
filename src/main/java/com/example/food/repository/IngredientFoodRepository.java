package com.example.food.repository;

import com.example.food.entity.IngredientFoodEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IngredientFoodRepository extends JpaRepository<IngredientFoodEntity, Long> {
    List<IngredientFoodEntity> findAllByFoodId(Long foodId);
    boolean existsByFoodIdAndIngredientId(Long foodId, Long ingredientId);
}
