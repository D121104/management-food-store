package com.example.food.entity;


import com.example.food.common.OrderStatus;
import com.example.food.common.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_order")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    private Long totalPrice;
    private LocalDateTime createdAt;
    private Double latitude;
    private Double longitude;
    private String address;
    private Long userId;
    private Long deliveryPrice;
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;
}
