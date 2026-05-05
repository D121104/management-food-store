package com.example.food.repository;

import com.example.food.entity.FoodEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoodRepository extends JpaRepository<FoodEntity, Long> {


    @Query("""
        SELECT DISTINCT f 
        FROM FoodEntity f
        JOIN FoodCategoryDetailEntity  fc
        ON f.id = fc.foodId
        WHERE fc.categoryDetailId IN :categoryDetailIds
                AND (:avgRating IS NULL OR f.avgRating >= :avgRating)
        """)
    Page<FoodEntity> findAllByFilter(@Param("categoryDetailIds") List<Long> categoryDetailIds,
                                     float avgRating,
                                     Pageable pageable);

    Page<FoodEntity> findAllByNameContainingIgnoreCase(String name, Pageable pageable);

    @Query("""
        SELECT f
        FROM FoodEntity f
        JOIN UserFoodLikeEntity ufl ON f.id = ufl.foodId
        WHERE ufl.userId = :userId
        ORDER BY f.id desc 
    """)
    Page<FoodEntity> findLikedFoods(Long userId, Pageable pageable);

    @Query("""
    SELECT DISTINCT f
    FROM FoodEntity f
    JOIN FoodCategoryDetailEntity fcd ON f.id = fcd.foodId
    JOIN CategoryDetailEntity cd ON fcd.categoryDetailId = cd.id
    WHERE cd.categoryId = :categoryId
""")
    Page<FoodEntity> findByCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);

    List<FoodEntity> findTop10ByOrderByTotalBoughtDesc();

    @Query("""
    SELECT f 
    FROM FoodEntity f
        JOIN OrderDetailEntity ode ON f.id = ode.foodId
        JOIN OrderEntity oe ON oe.id = ode.orderId
    WHERE oe.userId = :userId
    GROUP BY f
    ORDER BY SUM(ode.quantity) DESC
""")
    List<FoodEntity> findTopFoodBoughtOfUser(Long userId, Pageable pageable);

    @Query("""
    SELECT f FROM FoodEntity f
    WHERE f.id NOT IN :ids
""")
    List<FoodEntity> findRandomFoodsExcludeIds(List<Long> ids, Pageable pageable);

    @Query("""
    SELECT f FROM FoodEntity f
    WHERE f.id NOT IN :ids
""")
    List<FoodEntity> findFoodsExcludeIds(List<Long> ids, Pageable pageable);

    List<FoodEntity> findAllByIdIn(List<Long> foodIds);
}
