package com.example.food.controller;

import com.example.food.dto.ApiResponse;
import com.example.food.dto.request.user.CreateUserRequest;
import com.example.food.dto.request.user.UpdateUserRequest;
import com.example.food.dto.response.user.UserDetailResponse;
import com.example.food.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/users")
@Tag(name = "User Management", description = "API quản lý người dùng")
public class UserController {
    private final UserService userService;

    @Operation(summary = "Tạo mới người dùng")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<UserDetailResponse> createUser(@RequestBody @Valid CreateUserRequest request) {
        return ApiResponse.<UserDetailResponse>builder()
                .code(201)
                .message("Tạo người dùng thành công")
                .result(userService.createUser(request))
                .build();
    }

    @Operation(summary = "Lấy thông tin người dùng theo ID")
    @GetMapping("/{id}")
    public ApiResponse<UserDetailResponse> getUserById(@PathVariable Long id) {
        return ApiResponse.<UserDetailResponse>builder()
                .code(200)
                .message("Lấy thông tin người dùng thành công")
                .result(userService.getUserById(id))
                .build();
    }

    @Operation(summary = "Lấy tất cả người dùng (phân trang)")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Page<UserDetailResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ApiResponse.<Page<UserDetailResponse>>builder()
                .code(200)
                .message("Lấy danh sách người dùng thành công")
                .result(userService.getAllUsers(pageable))
                .build();
    }

    @Operation(summary = "Lấy tất cả người dùng")
    @GetMapping("/list/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<UserDetailResponse>> getAllUsersNoPage() {
        return ApiResponse.<List<UserDetailResponse>>builder()
                .code(200)
                .message("Lấy danh sách người dùng thành công")
                .result(userService.getAllUsers())
                .build();
    }

   
    @Operation(summary = "Tìm kiếm người dùng theo email")
    @GetMapping("/email/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<UserDetailResponse> getUserByEmail(@PathVariable String email) {
        return ApiResponse.<UserDetailResponse>builder()
                .code(200)
                .message("Tìm kiếm người dùng thành công")
                .result(userService.getUserByEmail(email))
                .build();
    }

    @Operation(summary = "Cập nhật thông tin người dùng")
    @PostMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<UserDetailResponse> updateUser(
            @PathVariable Long id,
            @ModelAttribute @Valid UpdateUserRequest request) {
        return ApiResponse.<UserDetailResponse>builder()
                .code(200)
                .message("Cập nhật người dùng thành công")
                .result(userService.updateUser(id, request))
                .build();
    }

    @Operation(summary = "Xóa người dùng theo ID")
    @DeleteMapping("/{id}")
    public ApiResponse<?> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ApiResponse.builder()
                .code(200)
                .message("Xóa người dùng thành công")
                .build();
    }

    
    @Operation(summary = "Tìm kiếm người dùng theo họ tên")
    @GetMapping("/search/name")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<UserDetailResponse>> searchUserByFullName(
            @RequestParam String fullName) {
        return ApiResponse.<List<UserDetailResponse>>builder()
                .code(200)
                .message("Tìm kiếm người dùng thành công")
                .result(userService.searchUserByFullName(fullName))
                .build();
    }
    
    @Operation(summary = "Lấy danh sách người dùng theo role")
    @GetMapping("/role/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<UserDetailResponse>> getUsersByRole(@PathVariable String role) {
        return ApiResponse.<List<UserDetailResponse>>builder()
                .code(200)
                .message("Lấy danh sách người dùng thành công")
                .result(userService.getUsersByRole(role))
                .build();
    }
}

