package com.example.food.controller;

import com.example.food.dto.ApiResponse;
import com.example.food.entity.CategoryEntity;
import com.example.food.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/category")
public class CategoryController {
    private final CategoryService categoryService;

    @Operation(summary = "Lấy ra tất cả danh mục sản phẩm")
    @GetMapping()
    public ApiResponse<List<CategoryEntity>> findAll() {
        return ApiResponse.<List<CategoryEntity>>builder()
                .code(200)
                .message("Lấy danh sách danh mục sản phẩm thành công")
                .result(categoryService.findAll())
                .build();
    }
}
