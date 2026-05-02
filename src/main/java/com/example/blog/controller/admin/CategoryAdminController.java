package com.example.blog.controller.admin;

import com.example.blog.annotation.OperationLog;
import com.example.blog.common.result.Result;
import com.example.blog.dto.category.CategoryDTO;
import com.example.blog.service.CategoryService;
import com.example.blog.vo.CategoryVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "后台-分类管理")
@RestController
@RequestMapping("/admin/category")
public class CategoryAdminController {

    private final CategoryService categoryService;

    public CategoryAdminController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    @Operation(summary = "新增分类")
    @OperationLog(module = "分类管理", operation = "新增分类")
    public Result<Void> add(@Valid @RequestBody CategoryDTO categoryDTO) {
        categoryService.add(categoryDTO);
        return Result.success();
    }

    @PutMapping
    @Operation(summary = "修改分类")
    @OperationLog(module = "分类管理", operation = "修改分类")
    public Result<Void> update(@Valid @RequestBody CategoryDTO categoryDTO) {
        categoryService.update(categoryDTO);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除分类")
    @OperationLog(module = "分类管理", operation = "删除分类")
    public Result<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询分类详情")
    public Result<CategoryVO> detail(@PathVariable Long id) {
        return Result.success(categoryService.getById(id));
    }

    @GetMapping("/list")
    @Operation(summary = "查询分类列表")
    public Result<List<CategoryVO>> list() {
        return Result.success(categoryService.list());
    }
}
