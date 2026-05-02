package com.example.blog.controller.admin;

import com.example.blog.annotation.OperationLog;
import com.example.blog.common.result.Result;
import com.example.blog.service.FileService;
import com.example.blog.vo.UploadFileVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "后台-文件管理")
@RestController
@RequestMapping("/admin/file")
public class FileAdminController {

    private final FileService fileService;

    public FileAdminController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/upload")
    @Operation(summary = "上传图片文件")
    @OperationLog(module = "文件管理", operation = "上传图片")
    public Result<UploadFileVO> upload(@RequestParam("file") MultipartFile file) {
        return Result.success(fileService.upload(file));
    }
}
