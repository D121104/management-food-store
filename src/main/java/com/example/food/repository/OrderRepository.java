package com.example.food.repository;

import com.example.food.common.OrderStatus;
import com.example.food.common.PaymentStatus;
import com.example.food.entity.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    OrderEntity findAllByIdAndUserId(Long orderId, Long userId);
    Page<OrderEntity> findAllByUserId(Long userId, Pageable pageable);

    Page<OrderEntity>  findAllByUserIdAndStatus(Long userId, OrderStatus status, Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE OrderEntity o SET o.status = :newStatus " +
            "WHERE o.id = :id AND o.status <> :newStatus")
    int updateStatusIfChanged(@Param("id") Long id, @Param("newStatus") PaymentStatus newStatus);

    boolean existsByIdAndUserId(Long orderId, Long userId);
}
