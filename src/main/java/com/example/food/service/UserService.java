package com.example.food.service;

import com.example.food.dto.request.user.CreateUserRequest;
import com.example.food.dto.request.user.UpdateUserRequest;
import com.example.food.dto.response.user.UserDetailResponse;
import com.example.food.entity.UserEntity;
import com.example.food.exception.AppException;
import com.example.food.exception.ErrorCode;
import com.example.food.mapper.UserMapper;
import com.example.food.repository.UserRepository;
import com.example.food.service.cloudinary.CloudinaryService;
import lombok.AllArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final CloudinaryService cloudinaryService;

    /**
     * Tạo mới người dùng
     */
    @Transactional
    public UserDetailResponse createUser(CreateUserRequest request) {
        if (Boolean.TRUE.equals(userRepository.existsByEmail(request.getEmail()))) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        String hashedPassword = BCrypt.hashpw(request.getPassword(), BCrypt.gensalt());

        UserEntity user = UserEntity.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(hashedPassword)
                .phoneNumber(request.getPhoneNumber())
                .dateOfBirth(request.getDateOfBirth())
                .role(request.getRole() != null ? request.getRole() : "USER")
                .build();

        UserEntity savedUser = userRepository.save(user);

        return userMapper.toUserDetailResponse(savedUser);
    }

    /**
     * Lấy thông tin người dùng theo ID
     */
    public UserDetailResponse getUserById(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return userMapper.toUserDetailResponse(user);
    }

    /**
     * Lấy tất cả người dùng
     */
    public Page<UserDetailResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userMapper::toUserDetailResponse);
    }

    public List<UserDetailResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toUserDetailResponse)
                .collect(Collectors.toList());
    }

    /**
     * Tìm kiếm người dùng theo email
     */
    public UserDetailResponse getUserByEmail(String email) {
        UserEntity user = userRepository.findByEmail(email);
        if (user == null) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        return userMapper.toUserDetailResponse(user);
    }

    /**
     * Cập nhật thông tin người dùng
     */
    @Transactional
    public UserDetailResponse updateUser(Long id, UpdateUserRequest request) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));


        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getDateOfBirth() != null) {
            user.setDateOfBirth(request.getDateOfBirth());
        }

        if (request.getImage() != null) {
            String imageUrl = cloudinaryService.uploadImage(request.getImage());
            user.setImageUrl(imageUrl);
        }

        UserEntity updatedUser = userRepository.save(user);
        return userMapper.toUserDetailResponse(updatedUser);
    }

    /**
     * Xóa người dùng
     */
    @Transactional
    public void deleteUser(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        userRepository.delete(user);
    }

    /**
     * Tìm kiếm người dùng theo họ tên (chứa)
     */
    public List<UserDetailResponse> searchUserByFullName(String fullName) {
        return userRepository.findByFullNameContainingIgnoreCase(fullName)
                .stream()
                .map(userMapper::toUserDetailResponse)
                .collect(Collectors.toList());
    }

    /**
     * Lấy danh sách người dùng theo role
     */
    public List<UserDetailResponse> getUsersByRole(String role) {
        return userRepository.findByRole(role)
                .stream()
                .map(userMapper::toUserDetailResponse)
                .collect(Collectors.toList());
    }
}
