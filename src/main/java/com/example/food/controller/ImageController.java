package com.example.food.controller;

import com.example.food.dto.ApiResponse;
import com.example.food.service.cloudinary.CloudinaryService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/images")
public class ImageController {
    private final CloudinaryService cloudinaryService;

    @PostMapping("upload")
    public ApiResponse<String> uploadImage(@RequestParam("file") MultipartFile file) {
        return ApiResponse.<String>builder()
                .code(200)
                .message("Upload ảnh thành công")
                .result(cloudinaryService.uploadImage(file))
                .build();
    }
}
