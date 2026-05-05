package com.example.food.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Currency {
    VND("vnd");

    private final String value;
}
