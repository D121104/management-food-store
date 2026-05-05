package com.example.food.mapper;

import com.example.food.dto.request.auth.SignUpRequest;
import com.example.food.dto.request.user.CreateUserRequest;
import com.example.food.dto.response.user.UserDetailResponse;
import com.example.food.dto.response.user.UserResponse;
import com.example.food.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper
public interface UserMapper {
    UserEntity getEntityFromRequest(SignUpRequest signUpRequest);
    
    UserEntity toUserEntity(CreateUserRequest request);
    
    UserDetailResponse toUserDetailResponse(UserEntity user);
    
    UserResponse toUserResponse(UserEntity user);
}
