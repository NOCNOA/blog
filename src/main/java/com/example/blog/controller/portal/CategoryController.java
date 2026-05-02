package com.example.blog.controller.portal;

import com.example.blog.common.result.Result;
import com.example.blog.service.CategoryService;
import com.example.blog.vo.CategoryVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "前台-分类展示")
@RestController
@RequestMapping("/category")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/list")
    @Operation(summary = "查询前台分类列表")
    public Result<List<CategoryVO>> list() {
        return Result.success(categoryService.list());
    }
}
