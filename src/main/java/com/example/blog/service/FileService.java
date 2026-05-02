package com.example.blog.service;

import com.example.blog.vo.UploadFileVO;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    UploadFileVO upload(MultipartFile file);
}
