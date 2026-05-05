package com.example.food.service.cloudinary;

import com.cloudinary.Cloudinary;
import com.example.food.exception.AppException;
import com.example.food.exception.ErrorCode;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
@AllArgsConstructor
public class CloudinaryService {
    private Cloudinary cloudinary;

    public String uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            Map<?, ?> data = this.cloudinary.uploader().upload(file.getBytes(), Map.of());
            return data.get("url").toString();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            throw new AppException(ErrorCode.UPLOAD_IMAGE_FAILED);
        }

    }

}
