package com.example.food.repository;

import com.example.food.entity.CommentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    List<CommentEntity> findAllByFoodIdIn(List<Long> foodIds);

    Page<CommentEntity> findAllByFoodId(Long foodId, Pageable pageable);

    boolean existsByUserIdAndId(Long userId, Long commentId);
}
