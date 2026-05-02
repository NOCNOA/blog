package com.example.blog.dto.article;

import jakarta.validation.constraints.NotNull;

public class ArticleStatusDTO {

    @NotNull(message = "文章ID不能为空")
    private Long id;

    @NotNull(message = "文章状态不能为空")
    private Integer status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
