package com.example.food.controller;

import com.example.food.common.OrderStatus;
import com.example.food.dto.ApiResponse;
import com.example.food.dto.request.order.CancelOrderRequest;
import com.example.food.dto.request.order.OrderRequest;
import com.example.food.dto.request.order.UpdateStatusRequest;
import com.example.food.dto.response.order.OrderResponse;
import com.example.food.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final OrderService orderService;

    @Operation(summary = "Đặt hàng")
    @PostMapping("create-order")
    public ApiResponse<OrderResponse> createOrder(@RequestBody OrderRequest orderRequest,
                                                  @RequestHeader("Authorization") String accessToken) {
        return ApiResponse.<OrderResponse>builder()
                .code(201)
                .message("Đặt hàng thành công")
                .result(orderService.createOrder(accessToken ,orderRequest))
                .build();
    }

    @Operation(summary = "Hủy đơn hàng")
    @PostMapping("cancel-order")
    public ApiResponse<OrderResponse> cancelOrder(@RequestBody CancelOrderRequest cancelOrderRequest,
                                                  @RequestHeader("Authorization") String accessToken) {
        return ApiResponse.<OrderResponse>builder()
                .code(200)
                .message("Hủy đơn hàng thành công")
                .result(orderService.cancelOrder(accessToken, cancelOrderRequest))
                .build();
    }

    @Operation(summary = "Lấy đơn hàng theo userId và status")
    @GetMapping("get-orders")
    public ApiResponse<Page<OrderResponse>> getOrderByUserId(@RequestParam(value = "status", required = false) OrderStatus status,
                                                             @ParameterObject Pageable pageable,
                                                             @RequestHeader("Authorization") String accessToken) {
        Page<OrderResponse> orderResponsePage;
        if (status == null) {
            orderResponsePage = orderService.getOrdersByUserId(accessToken ,pageable);
        } else {
            orderResponsePage = orderService.getOrdersByUserIdAndStatus(accessToken, pageable,  status);
        }

        return ApiResponse.<Page<OrderResponse>>builder()
                .code(200)
                .message("Lấy đơn hàng của người dùng thành công")
                .result(orderResponsePage)
                .build();
    }

    @Operation(summary = "Lấy đơn hàng theo order id")
    @GetMapping("{orderId}")
    public ApiResponse<OrderResponse> getOrderById(@RequestHeader("Authorization") String accessToken,
                                                   @PathVariable("orderId") Long orderId) {

        return ApiResponse.<OrderResponse>builder()
                .code(200)
                .message("Lấy đơn hàng thành công")
                .result(orderService.getOrderById(accessToken, orderId))
                .build();
    }

    @Operation(summary = "Cập nhật trạng thái đơn hàng")
    @PostMapping("update-order-status")
    public ApiResponse<OrderResponse> updateOrderStatus(@RequestBody UpdateStatusRequest updateStatusRequest,
                                                        @RequestHeader("Authorization") String accessToken) {
        return ApiResponse.<OrderResponse>builder()
                .code(200)
                .message("Cập nhật trạng thái đơn hàng thành công")
                .result(orderService.updateOrderStatus(accessToken, updateStatusRequest))
                .build();
    }

}
