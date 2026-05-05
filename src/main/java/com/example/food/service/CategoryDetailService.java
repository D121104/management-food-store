package com.example.food.service;

import com.example.food.entity.CategoryDetailEntity;
import com.example.food.repository.CategoryDetailRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@AllArgsConstructor
@Service
public class CategoryDetailService {
    private final CategoryDetailRepository  categoryDetailRepository;

    @Transactional(readOnly = true)
    public List<CategoryDetailEntity> findAllByCategoryId(Long categoryId) {
        return categoryDetailRepository.findAllByCategoryId(categoryId);
    }
}
