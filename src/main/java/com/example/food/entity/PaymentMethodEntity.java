package com.example.food.entity;

import com.example.food.common.CardBrand;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_payment_method")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentMethodEntity {
    @Id
    private String id;

    private Long userId;
    private String holderName;
    @Enumerated(EnumType.STRING)
    private CardBrand cardBrand;
    private String last4;
    private Long exp_month;
    private Long exp_year;

}
