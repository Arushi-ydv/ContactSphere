package com.contactsphere.services.impl;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.contactsphere.services.CloudinaryService;

@Service
public class CloudinaryServiceImple implements CloudinaryService {
    
    @Autowired
    private Cloudinary cloudinary;

    public String uploadImage(MultipartFile file) {
        try {
            Map result = cloudinary.uploader().upload(
                file.getBytes(), 
                Map.of("folder", "contactsphere/profile-pictures")
            );
            return result.get("secure_url").toString();
        } catch (IOException e) {

            throw new RuntimeException("Image upload failed", e);
        }
    }
}
