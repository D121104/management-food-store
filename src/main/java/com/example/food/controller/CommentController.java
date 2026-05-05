package com.example.food.controller;

import com.example.food.dto.ApiResponse;
import com.example.food.dto.request.comment.CommentRequest;
import com.example.food.dto.response.comment.CommentResponse;
import com.example.food.security.TokenHelper;
import com.example.food.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/comments")
public class CommentController {
    private final CommentService commentService;

    @Operation(summary = "Thực hiện comment vào food đã mua")
    @PostMapping
    public ApiResponse<?> commentFood(@RequestHeader("Authorization") String accessToken,
                                      @RequestBody @Valid CommentRequest commentRequest) {
        commentService.comment(accessToken, commentRequest);
        return ApiResponse.builder()
                .code(200)
                .message("Đánh giá sản phẩm thành công")
                .build();
    }

    @Operation(summary = "Lấy ra comment theo food")
    @GetMapping("/{foodId}")
    public ApiResponse<Page<CommentResponse>> getCommentsByFood(@RequestHeader("Authorization") String accessToken,
                                                                @PathVariable Long foodId,
                                                                @ParameterObject Pageable pageable) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        return ApiResponse.<Page<CommentResponse>>builder()
                .code(200)
                .message("Lấy comment thành công")
                .result(commentService.getCommentsByFood(foodId, pageable, userId))
                .build();
    }

    @Operation(summary = "Xóa comment")
    @DeleteMapping("/{commentId}")
    public ApiResponse<?> deleteComment(@PathVariable Long commentId,
                                        @RequestHeader("Authorization") String accessToken) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        commentService.deleteComment(commentId, userId);
        return ApiResponse.builder()
                .code(200)
                .message("Xóa comment thành công")
                .build();
    }

    @Operation(summary = "Thay đổi comment")
    @PutMapping("/{commentId}")
    public ApiResponse<?> updateComment(@RequestHeader("Authorization") String accessToken,
                                        @RequestBody @Valid CommentRequest commentRequest,
                                        @PathVariable Long commentId) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        commentService.updateComment(commentId, commentRequest, userId);
        return ApiResponse.builder()
                .code(200)
                .message("Thay đổi comment thành công")
                .build();
    }
}
