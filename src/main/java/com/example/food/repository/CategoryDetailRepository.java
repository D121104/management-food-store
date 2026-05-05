package com.example.food.repository;

import com.example.food.entity.CategoryDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryDetailRepository extends JpaRepository<CategoryDetailEntity, Long> {
    List<CategoryDetailEntity> findAllByCategoryId(Long categoryId);
}
