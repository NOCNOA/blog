package com.example.blog.service.impl;

import com.example.blog.common.exception.BusinessException;
import com.example.blog.common.result.ResultCode;
import com.example.blog.config.FileStorageProperties;
import com.example.blog.service.FileService;
import com.example.blog.vo.UploadFileVO;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024L;

    private final FileStorageProperties fileStorageProperties;

    public FileServiceImpl(FileStorageProperties fileStorageProperties) {
        this.fileStorageProperties = fileStorageProperties;
    }

    @Override
    public UploadFileVO upload(MultipartFile file) {
        validateFile(file);

        String originalFilename = file.getOriginalFilename();
        String extension = getExtension(originalFilename);
        String dateFolder = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        String savedName = UUID.randomUUID().toString().replace("-", "") + extension;

        Path rootDir = Paths.get(fileStorageProperties.getUploadDir()).toAbsolutePath().normalize();
        Path targetDir = rootDir.resolve(dateFolder).normalize();
        Path targetFile = targetDir.resolve(savedName).normalize();

        try {
            Files.createDirectories(targetDir);
            Files.copy(file.getInputStream(), targetFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new BusinessException(ResultCode.INTERNAL_ERROR.getCode(), "文件上传失败");
        }

        String relativePath = dateFolder + "/" + savedName;
        UploadFileVO vo = new UploadFileVO();
        vo.setOriginalName(originalFilename);
        vo.setFileName(savedName);
        vo.setFileSize(file.getSize());
        vo.setContentType(file.getContentType());
        vo.setRelativePath(relativePath);
        vo.setUrl(fileStorageProperties.getAccessUrlPrefix() + relativePath);
        return vo;
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "上传文件不能为空");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "文件大小不能超过5MB");
        }
        String contentType = file.getContentType();
        if (!StringUtils.hasText(contentType)) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "无法识别文件类型");
        }
        String lowerContentType = contentType.toLowerCase(Locale.ROOT);
        if (!lowerContentType.startsWith("image/")) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "当前仅支持图片上传");
        }
    }

    private String getExtension(String fileName) {
        if (!StringUtils.hasText(fileName) || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }
}
