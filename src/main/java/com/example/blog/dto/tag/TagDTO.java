package com.example.blog.dto.tag;

import jakarta.validation.constraints.NotBlank;

public class TagDTO {

    private Long id;

    @NotBlank(message = "标签名称不能为空")
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
