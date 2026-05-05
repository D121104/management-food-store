package com.example.food.controller;

import com.example.food.dto.ApiResponse;
import com.example.food.dto.request.address.AddressRequest;
import com.example.food.dto.response.address.AddressResponse;
import com.example.food.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/addresses")
@AllArgsConstructor
public class AddressController {
    private final AddressService addressService;

    @Operation(summary = "Thêm địa chỉ")
    @PostMapping
    public ApiResponse<?> createAddress(@RequestBody @Valid AddressRequest addressRequest,
                                        @RequestHeader("Authorization") String accessToken) {

        addressService.createAddress(addressRequest, accessToken);

        return ApiResponse.builder()
                .code(200)
                .message("Thêm địa chỉ thành công")
                .build();
    }

    @Operation(summary = "Thay đổi địa chỉ")
    @PutMapping("/{addressId}")
    public ApiResponse<?> updateAddress(@RequestBody @Valid AddressRequest addressRequest,
                                        @RequestHeader("Authorization") String accessToken,
                                        @PathVariable Long addressId) {

        addressService.updateAddress(addressRequest, accessToken, addressId);

        return ApiResponse.builder()
                .code(200)
                .message("Thêm địa chỉ thành công")
                .build();
    }

    @Operation(summary = "Lấy ra địa chỉ cụ thể")
    @GetMapping("/{addressId}")
    public ApiResponse<AddressResponse> getAddress(@PathVariable Long addressId,
                                                   @RequestHeader("Authorization") String accessToken) {

        return ApiResponse.<AddressResponse>builder()
                .code(200)
                .message("Lấy địa chỉ cụ thể thành công")
                .result(addressService.getAddress(addressId, accessToken))
                .build();
    }

    @Operation(summary = "Lấy ra địa chỉ cụ thể")
    @GetMapping("/user")
    public ApiResponse<List<AddressResponse>> getAddressListByUser(@RequestHeader("Authorization") String accessToken) {

        return ApiResponse.<List<AddressResponse>>builder()
                .code(200)
                .message("Lấy địa chỉ cụ thể")
                .result(addressService.getAddressByUser(accessToken))
                .build();
    }

    @Operation(summary = "Xóa address")
    @DeleteMapping("/{addressId}")
    public ApiResponse<?> deleteAddress(@PathVariable Long addressId,
                                        @RequestHeader("Authorization") String accessToken) {
        addressService.deleteAddress(addressId, accessToken);
        return ApiResponse.builder()
                .code(200)
                .message("Xóa địa chỉ thành công")
                .build();
    }
}
