package com.example.food.service;

import com.example.food.entity.CategoryEntity;
import com.example.food.repository.CategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<CategoryEntity> findAll() {
        return categoryRepository.findAll();
    }
}
