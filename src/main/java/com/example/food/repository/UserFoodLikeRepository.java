package com.example.food.repository;

import com.example.food.entity.UserFoodLikeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserFoodLikeRepository extends JpaRepository<UserFoodLikeEntity, Long> {
    boolean existsByUserIdAndId(Long userId, Long id);

    List<UserFoodLikeEntity> findAllByUserId(Long userId);


    boolean existsByUserIdAndFoodId(Long userId, Long foodId);

    void deleteByUserIdAndFoodId(Long userId, Long foodId);
}
