package com.example.food.controller;

import com.example.food.dto.ApiResponse;
import com.example.food.dto.request.food.FilterFoodRequest;
import com.example.food.dto.response.food.FoodResponse;
import com.example.food.service.FoodService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/foods")
public class FoodController {
    private final FoodService foodService;

    @Operation(summary = "Lấy ra food theo filter")
    @PostMapping("/filter")
    public ApiResponse<Page<FoodResponse>> searchByFilter(@RequestBody FilterFoodRequest filterFoodRequest,
                                                          @ParameterObject Pageable pageable,
                                                          @RequestHeader("Authorization") String accessToken) {
        return ApiResponse.<Page<FoodResponse>>builder()
                .code(200)
                .message("Lấy danh sách food theo filter thành công")
                .result(foodService.searchByFilter(pageable, filterFoodRequest, accessToken))
                .build();
    }

    @Operation(summary = "Lấy ra food theo tên")
    @GetMapping("/{name}")
    public ApiResponse<Page<FoodResponse>> searchFoodsByName(@PathVariable String name,
                                                             @ParameterObject Pageable pageable,
                                                             @RequestHeader("Authorization") String accessToken) {
        return ApiResponse.<Page<FoodResponse>>builder()
                .code(200)
                .message("Tìm kiếm món ăn thành công")
                .result(foodService.searchByName(name, pageable, accessToken))
                .build();
    }

    @Operation(summary = "Lấy ra food trong danh sách yêu thích")
    @GetMapping("/likes")
    public ApiResponse<Page<FoodResponse>> getFoodsLike(@RequestHeader("Authorization") String accessToken,
                                                        @ParameterObject Pageable pageable) {
        return ApiResponse.<Page<FoodResponse>>builder()
                .code(200)
                .message("Lấy danh sách món ăn yêu thích thành công")
                .result(foodService.getFoodsLike(accessToken, pageable))
                .build();
    }

    @Operation(summary = "Thêm food vào danh sách yêu thích")
    @PostMapping("/{foodId}/likes")
    public ApiResponse<?> likeFood(@PathVariable Long foodId,
                                   @RequestHeader("Authorization") String accessToken) {
        foodService.likeFood(accessToken, foodId);
        return ApiResponse.builder()
                .code(200)
                .message("Thêm món ăn vào danh sách yêu thích thành công")
                .build();
    }

    @Operation(summary = "Xóa food vào danh sách yêu thích")
    @DeleteMapping("/{userFoodLikeId}/unlikes")
    public ApiResponse<?> unlikeFood(@PathVariable Long userFoodLikeId,
                                   @RequestHeader("Authorization") String accessToken) {
        foodService.unlikeFood(accessToken, userFoodLikeId);
        return ApiResponse.builder()
                .code(200)
                .message("Xóa món ăn vào danh sách yêu thích thành công")
                .build();
    }

    @Operation(summary = "Xem chi tiết food")
    @GetMapping("/details/{foodId}")
    public ApiResponse<FoodResponse> getFoodById(@PathVariable Long foodId,
                                                 @RequestHeader("Authorization") String accessToken) {
        return ApiResponse.<FoodResponse>builder()
                .code(200)
                .message("Xem chi tiết food thành công")
                .result(foodService.getFoodDetailById(foodId, accessToken))
                .build();
    }

    @Operation(summary = "Lấy food theo category")
    @GetMapping("/category/{categoryId}")
    public ApiResponse<Page<FoodResponse>> getFoodsByCategory(@PathVariable Long categoryId,
                                                              @ParameterObject Pageable pageable,
                                                              @RequestHeader("Authorization") String accessToken) {
        return ApiResponse.<Page<FoodResponse>>builder()
                .code(200)
                .message("Lấy danh sách món ăn theo danh mục thành công")
                .result(foodService.getFoodsByCategory(categoryId, pageable, accessToken))
                .build();
    }

    @Operation(summary = "Lấy ra food best seller")
    @GetMapping("/best-seller")
    public ApiResponse<List<FoodResponse>> getBestSellerFoods(@RequestHeader("Authorization") String accessToken) {
        return ApiResponse.<List<FoodResponse>>builder()
                .code(200)
                .message("Lấy danh sách best seller thành công")
                .result(foodService.getBestSeller(accessToken))
                .build();
    }

    @Operation(summary = "Lấy ra food recommend")
    @GetMapping("/recommend")
    public ApiResponse<List<FoodResponse>> getRecommendFoods(@RequestHeader("Authorization") String accessToken) {
        return ApiResponse.<List<FoodResponse>>builder()
                .code(200)
                .message("Lấy ra danh sách food recommend thành công")
                .result(foodService.getRecommendFoods(accessToken))
                .build();
    }

    @Operation(summary = "Xóa yêu thích food theo userId và foodId")
    @DeleteMapping("/{foodId}/likes/remove")
    public ApiResponse<?> removeLikeFoodByUser(@PathVariable Long foodId,
                                               @RequestHeader("Authorization") String accessToken) {
        foodService.removeLikeFoodByUserIdAndFoodId(accessToken, foodId);

        return ApiResponse.builder()
                .code(200)
                .message("Xóa món ăn khỏi danh sách yêu thích thành công")
                .build();
    }
}
