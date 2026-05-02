package com.example.blog.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "blog.file")
public class FileStorageProperties {

    private String uploadDir = "uploads";
    private String accessPath = "/uploads/**";
    private String accessUrlPrefix = "/uploads/";

    public String getUploadDir() {
        return uploadDir;
    }

    public void setUploadDir(String uploadDir) {
        this.uploadDir = uploadDir;
    }

    public String getAccessPath() {
        return accessPath;
    }

    public void setAccessPath(String accessPath) {
        this.accessPath = accessPath;
    }

    public String getAccessUrlPrefix() {
        return accessUrlPrefix;
    }

    public void setAccessUrlPrefix(String accessUrlPrefix) {
        this.accessUrlPrefix = accessUrlPrefix;
    }
}
