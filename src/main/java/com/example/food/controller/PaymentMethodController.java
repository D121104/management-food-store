package com.example.food.controller;

import com.example.food.dto.ApiResponse;
import com.example.food.dto.request.payment_method.CreatePaymentMethodRequest;
import com.example.food.dto.request.payment_method.UpdatePaymentMethodRequest;
import com.example.food.dto.response.payment_method.PaymentMethodResponse;
import com.example.food.service.PaymentMethodService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/payment-method")
public class PaymentMethodController {
    private PaymentMethodService paymentMethodService;

    @Operation(summary = "Lấy thông tin phương thức thanh toán")
    @GetMapping("/{pmId}")
    public ApiResponse<PaymentMethodResponse> getPaymentMethodDetails(
            @RequestHeader("Authorization") String accessToken,
            @PathVariable("pmId")  String pmID) {
        return ApiResponse.<PaymentMethodResponse>builder()
                .code(200)
                .message("Lấy thông tin phương thức thanh toán thành công")
                .result(paymentMethodService.getPaymentMethodDetailById(accessToken, pmID))
                .build();
    }

    @Operation(summary = "Lấy danh sách phương thức thanh toán")
    @GetMapping()
    public ApiResponse<List<PaymentMethodResponse>> getAllPaymentMethods(
            @RequestHeader("Authorization") String accessToken) {
        return ApiResponse.<List<PaymentMethodResponse>>builder()
                .code(200)
                .message("Lấy danh sách phương thức thanh toán của người dùng thành công")
                .result(paymentMethodService.findAllPaymentMethods(accessToken))
                .build();
    }

    @Operation(summary = "Tạo phương thức thanh toán")
    @PostMapping("")
    public ApiResponse<PaymentMethodResponse> createPaymentMethod(
            @RequestBody CreatePaymentMethodRequest createPaymentMethodRequest,
            @RequestHeader("Authorization") String accessToken) {
        return ApiResponse.<PaymentMethodResponse>builder()
                .code(201)
                .message("Tạo phương thức thanh toán thành công")
                .result(paymentMethodService.createPaymentMethod(accessToken, createPaymentMethodRequest))
                .build();
    }

    // Chỉ cập nhật holder name
    @Operation(summary = "Cập nhật phương thức thanh toán")
    @PostMapping("update-payment-method")
    public ApiResponse<PaymentMethodResponse> updatePaymentMethod(
            @RequestBody UpdatePaymentMethodRequest updatePaymentMethodRequest,
            @RequestHeader("Authorization") String accessToken) {
        return ApiResponse.<PaymentMethodResponse>builder()
                .code(200)
                .message("Cập nhật phương thức thanh toán thành công")
                .result(paymentMethodService.updatePaymentMethodBillingDetails(
                        accessToken,
                        updatePaymentMethodRequest))
                .build();
    }

    @Operation(summary = "Xóa phương thức thanh toán")
    @DeleteMapping("/{pmId}")
    public ApiResponse<PaymentMethodResponse> deletePaymentMethod(
            @PathVariable("pmId") String pmID,
            @RequestHeader("Authorization") String accessToken) {
        return ApiResponse.<PaymentMethodResponse>builder()
                .code(200)
                .message("Xóa phương thức thanh toán thành công")
                .result(paymentMethodService.deletePaymentMethod(accessToken, pmID))
                .build();
    }



}
