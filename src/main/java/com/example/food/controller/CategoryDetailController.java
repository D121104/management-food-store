package com.example.food.controller;

import com.example.food.dto.ApiResponse;
import com.example.food.entity.CategoryDetailEntity;
import com.example.food.service.CategoryDetailService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/category/details")
public class CategoryDetailController {
    private final CategoryDetailService categoryDetailService;

    @Operation(summary = "Lấy chi tiết danh mục sản phẩm")
    @GetMapping("/{categoryId}")
    public ApiResponse<List<CategoryDetailEntity>> findAll(@PathVariable Long categoryId) {
        return ApiResponse.<List<CategoryDetailEntity>>builder()
                .code(200)
                .message("Lấy chi tiết danh mục sản phẩm thành công")
                .result(categoryDetailService.findAllByCategoryId(categoryId))
                .build();
    }
}
