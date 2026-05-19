package com.yougame.controller;

import com.yougame.common.result.Result;
import com.yougame.common.utils.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
@Slf4j
public class FileUploadController {

    @Value("${file.upload.path}")
    private String uploadPath;

    @Value("${file.upload.access-path}")
    private String accessPath;

    @PostMapping("/image")
    public Result<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return Result.error(401, "请先登录");
        }

        // 1. 校验
        if (file.isEmpty()) {
            return Result.error(400, "请选择文件");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return Result.error(400, "只允许上传图片文件");
        }
        if (file.getSize() > 5 * 1024 * 1024) {
            return Result.error(400, "图片大小不能超过5MB");
        }

        // 2. 生成文件名
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String newFileName = datePath + "/" + UUID.randomUUID() + extension;

        // 3. 保存文件（路径由配置注入，已经是绝对路径）
        Path filePath = Paths.get(uploadPath, newFileName);
        try {
            Files.createDirectories(filePath.getParent());   // 自动创建日期子目录
            file.transferTo(filePath.toFile());
        } catch (IOException e) {
            log.error("文件上传失败", e);
            return Result.error(500, "文件上传失败，请稍后重试");
        }

        // 4. 返回可访问的 URL
        String imageUrl = accessPath + newFileName;
        log.info("用户 {} 上传图片：{}", userId, imageUrl);

        Map<String, String> result = new HashMap<>();
        result.put("url", imageUrl);
        return Result.success("上传成功", result);
    }
}