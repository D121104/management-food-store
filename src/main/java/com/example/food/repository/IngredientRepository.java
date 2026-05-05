package com.example.food.repository;

import com.example.food.entity.IngredientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IngredientRepository extends JpaRepository<IngredientEntity, Long> {

    @Query("""
        SELECT i from IngredientEntity i
        JOIN IngredientFoodEntity ife 
        ON i.id = ife.ingredientId
        WHERE ife.foodId = :foodId
    """)
    List<IngredientEntity> findAllByFood(@Param("foodId") Long foodId);
}
