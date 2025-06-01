package com.example.PetApp.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
public class FileUploadUtil {
    public static String fileUpload(MultipartFile imageUrl, String uploadDir, String imageKind) {
        UUID uuid = UUID.randomUUID();//기본 이미지를 넣어야할듯.
        String imageFileName;
        if (imageUrl.isEmpty()) {
            return "/basic/Profile_avatar_placeholder_large.png";
        }else {
            imageFileName = uuid + "_" + imageUrl.getOriginalFilename();
            try {
                Path path = Paths.get(uploadDir, imageFileName);
                Files.copy(imageUrl.getInputStream(), path);
            } catch (IOException e) {
                log.error("사진 저장중 에러");
                throw new RuntimeException(e);
            }
            return "/"+imageKind+"/"+imageFileName;
        }
    }
}
