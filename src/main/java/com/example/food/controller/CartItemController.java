package com.example.food.controller;

import com.example.food.dto.ApiResponse;
import com.example.food.dto.request.cart.CartItemRequest;
import com.example.food.dto.request.cart.UpdateCartItemRequest;
import com.example.food.dto.response.cart.CartItemResponse;
import com.example.food.dto.response.cart.CartResponse;
import com.example.food.service.CartItemService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/cart-items")
public class CartItemController {
    private CartItemService cartItemService;

    @GetMapping("")
    public ApiResponse<CartResponse> getCartItems(
            @RequestHeader("Authorization") String accessToken) {
        return ApiResponse.<CartResponse>builder()
                .code(200)
                .message("Lấy danh sách vật phẩm trong giỏ hàng thành công")
                .result(cartItemService.getCartItems(accessToken))
                .build();
    }

    @PostMapping("add-item")
    public ApiResponse<CartItemResponse> addCartItem(
            @RequestHeader("Authorization")  String accessToken,
            @RequestBody CartItemRequest cartItemRequest) {
        return ApiResponse.<CartItemResponse>builder()
                .code(200)
                .message("Thêm vật phẩm vào giỏ hàng thành công")
                .result(cartItemService.addCartItem(accessToken, cartItemRequest))
                .build();
    }

    @PostMapping("update-cart-item")
    public ApiResponse<CartItemResponse> updateCartItem(
            @RequestHeader("Authorization") String accessToken,
            @RequestBody UpdateCartItemRequest updateCartItemRequest) {
        return ApiResponse.<CartItemResponse>builder()
                .code(200)
                .message("Cập nhật vật phẩm trong giỏ hàng thành công")
                .result(cartItemService.updateCartItems(accessToken, updateCartItemRequest))
                .build();
    }

    @DeleteMapping("delete-cart-item/{cartId}")
    public ApiResponse<CartItemResponse> deleteCartItem(
            @RequestHeader("Authorization") String accessToken,
            @PathVariable("cartId") Long cartItemId) {
        return ApiResponse.<CartItemResponse>builder()
                .code(200)
                .message("Xóa vật phẩm thành công")
                .result(cartItemService.deleteCartItems(accessToken, cartItemId))
                .build();
    }
}
