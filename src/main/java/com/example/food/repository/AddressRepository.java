package com.example.food.repository;

import com.example.food.entity.AddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<AddressEntity, Long> {
    boolean existsByUserIdAndId(Long userId, Long addressId);

    List<AddressEntity> findAllByUserId(Long userId);

    AddressEntity findByUserIdAndId(Long userId, Long addressId);
}
