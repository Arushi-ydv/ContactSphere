package com.contactsphere.services.impl;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.contactsphere.services.CloudinaryService;

@Service
public class CloudinaryServiceImpl implements CloudinaryService {
    
    private final Cloudinary cloudinary;

    public CloudinaryServiceImpl(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    @Override
    public String uploadImage(MultipartFile file) {

        if(file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Please select an image");
        }

        if(file.getSize() > 5 * 1024 *1024) {
            throw new IllegalArgumentException("Image size must be less than 5 MB");
        }

        String contentType = file.getContentType();

        if(contentType == null ||
                !Set.of("image/jpeg", "image/png", "image/webp").contains(contentType)) {
                    throw new IllegalArgumentException("Only JPG, PNG, and WebP images are allowed");
                }

        try {
            Map<String, Object> result = cloudinary.uploader().upload(
                file.getBytes(), 
                Map.of("folder", "contactsphere/profile-pictures")
            ); 
            return result.get("secure_url").toString();
        } catch (IOException e) {

            throw new IllegalStateException("Image upload failed", e);
        }
    }
}
