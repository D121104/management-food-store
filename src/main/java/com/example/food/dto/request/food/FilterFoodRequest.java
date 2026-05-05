package com.example.food.dto.request.food;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class FilterFoodRequest {
    private List<Long> categoryDetailIds;
    private int rating;
}
