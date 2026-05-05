package com.example.food.repository;

import com.example.food.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    boolean existsByEmail(String email);
    UserEntity findByEmail(String email);
    List<UserEntity> findByFullNameContainingIgnoreCase(String fullName);
    List<UserEntity> findByRole(String role);
    List<UserEntity> findAllByIdIn(List<Long> userIds);
}
