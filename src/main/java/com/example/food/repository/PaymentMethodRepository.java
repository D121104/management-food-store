package com.example.food.repository;

import com.example.food.entity.PaymentMethodEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethodEntity,String> {
    List<PaymentMethodEntity> findAllByUserId(Long userId);
}
