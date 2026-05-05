package com.example.food.service;

import com.example.food.common.OrderStatus;
import com.example.food.dto.request.comment.CommentRequest;
import com.example.food.dto.response.comment.CommentResponse;
import com.example.food.entity.CommentEntity;
import com.example.food.entity.FoodEntity;
import com.example.food.entity.OrderEntity;
import com.example.food.entity.UserEntity;
import com.example.food.exception.AppException;
import com.example.food.exception.ErrorCode;
import com.example.food.repository.*;
import com.example.food.security.TokenHelper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final FoodRepository foodRepository;
    private final UserRepository userRepository;


    @Transactional
    public void comment(String accessToken, CommentRequest commentRequest) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);

        OrderEntity orderEntity = orderRepository.findAllByIdAndUserId(commentRequest.getOrderId(), userId);

        if (orderEntity == null) {
            throw new AppException(ErrorCode.NOT_FOUND_ORDER);
        }

        if (!orderEntity.getStatus().equals(OrderStatus.COMPLETED)) {
            throw new AppException(ErrorCode.ORDER_NOT_COMPLETED);
        }

        if (!orderDetailRepository.existsByOrderIdAndFoodId(commentRequest.getOrderId(), commentRequest.getFoodId())) {
            throw new AppException(ErrorCode.FOOD_NOT_IN_ORDER);
        }

        FoodEntity foodEntity = foodRepository.findById(commentRequest.getFoodId()).orElseThrow(
                () -> new AppException(ErrorCode.FOOD_NOT_FOUND)
        );

        if (foodEntity.getAvgRating() == null) {
            foodEntity.setTotalComments(1);
            double avg = Math.round(commentRequest.getRating() * 100.0) / 100.0;
            foodEntity.setAvgRating(avg);
        } else {
            double avg = (foodEntity.getAvgRating() * foodEntity.getTotalComments() + commentRequest.getRating())
                    /(foodEntity.getTotalComments() + 1);

            avg = Math.round(avg * 100.0) / 100.0;

            foodEntity.setTotalComments(foodEntity.getTotalComments() + 1);
            foodEntity.setAvgRating(avg);
        }

        foodRepository.save(foodEntity);

        CommentEntity commentEntity = CommentEntity.builder()
                .userId(userId)
                .foodId(commentRequest.getFoodId())
                .comment(commentRequest.getComment())
                .rating(commentRequest.getRating())
                .createdAt(LocalDateTime.now(
                        ZoneId.of("Asia/Ho_Chi_Minh")))
                .build();

        commentRepository.save(commentEntity);
    }

    @Transactional(readOnly = true)
    public Page<CommentResponse> getCommentsByFood(Long foodId, Pageable pageable, Long userId) {
        Page<CommentEntity> commentEntities = commentRepository.findAllByFoodId(foodId, pageable);

        List<UserEntity> userEntities = userRepository.findAllByIdIn(
                commentEntities.stream().map(CommentEntity::getUserId).toList()
        );

        Map<Long, UserEntity> userMap = userEntities.stream()
                .collect(Collectors.toMap(UserEntity::getId, Function.identity()));

        return commentEntities.map(
                commentEntity -> {
                    UserEntity userEntity = userMap.get(commentEntity.getUserId());

                    if (userEntity == null) {
                        throw new AppException(ErrorCode.USER_NOT_EXISTED);
                    }

                    CommentResponse commentResponse = CommentResponse.builder()
                            .userId(userEntity.getId())
                            .fullName(userEntity.getFullName())
                            .comment(commentEntity.getComment())
                            .rating(commentEntity.getRating())
                            .createdAt(commentEntity.getCreatedAt())
                            .isMe(
                                    userId == commentEntity.getUserId() ? true : false
                            )
                            .build();
                    return  commentResponse;
                }
        );
    }

    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        if (!commentRepository.existsByUserIdAndId(userId, commentId)) {
            throw  new AppException(ErrorCode.COMMENT_NOT_FOUND);
        }

        CommentEntity commentEntity = commentRepository.findById(commentId).orElseThrow(
                () -> new AppException(ErrorCode.COMMENT_NOT_FOUND)
        );


        FoodEntity foodEntity = foodRepository.findById(commentEntity.getFoodId()).orElseThrow(
                () -> new AppException(ErrorCode.FOOD_NOT_FOUND)
        );

        if (foodEntity.getTotalComments() == 1) {
            foodEntity.setTotalComments(0);
            foodEntity.setAvgRating(null);
        } else {
            double total = foodEntity.getAvgRating() * foodEntity.getTotalComments();

            total = total - commentEntity.getRating();

            int totalComment = foodEntity.getTotalComments() - 1;

            double newAvg = total / totalComment;

            newAvg = Math.round(newAvg * 100.0) / 100.0;

            foodEntity.setAvgRating(newAvg);
            foodEntity.setTotalComments(foodEntity.getTotalComments() - 1);
        }
        foodRepository.save(foodEntity);

        commentRepository.deleteById(commentId);
    }

    @Transactional
    public void updateComment(Long commentId, CommentRequest commentRequest, Long userId) {
        CommentEntity commentEntity = commentRepository.findById(commentId).orElseThrow(
                () -> new AppException(ErrorCode.COMMENT_NOT_FOUND)
        );

        if (commentEntity.getUserId() != userId) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        if (!orderRepository.existsByIdAndUserId(commentRequest.getOrderId(), userId)) {
            throw new AppException(ErrorCode.ORDER_NOT_FOUND);
        }

        FoodEntity  foodEntity = foodRepository.findById(commentRequest.getFoodId()).orElseThrow(
                () -> new AppException(ErrorCode.FOOD_NOT_FOUND)
        );

        double total = foodEntity.getAvgRating() * foodEntity.getTotalComments();

        total = total - commentEntity.getRating() + commentRequest.getRating();

        double newAvg = total / foodEntity.getTotalComments();

        newAvg = Math.round(newAvg * 100.0) / 100.0;

        commentEntity.setComment(commentRequest.getComment());
        commentEntity.setRating(commentRequest.getRating());

        foodEntity.setAvgRating(newAvg);
        foodRepository.save(foodEntity);

        commentRepository.save(commentEntity);
    }
}
