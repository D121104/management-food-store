package com.example.food.dto.request.address;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressRequest {
    @NotBlank(message = "Địa chỉ không được để trống")
    private String address;
    @NotNull
    private Double latitude;
    @NotNull
    private Double longitude;
}
