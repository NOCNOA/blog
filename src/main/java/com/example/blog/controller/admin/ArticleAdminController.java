package com.example.blog.controller.admin;

import com.example.blog.annotation.OperationLog;
import com.example.blog.common.result.PageResult;
import com.example.blog.common.result.Result;
import com.example.blog.dto.article.ArticleSaveDTO;
import com.example.blog.dto.article.ArticleStatusDTO;
import com.example.blog.service.ArticleService;
import com.example.blog.vo.ArticleAdminListVO;
import com.example.blog.vo.ArticleDetailVO;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "后台-文章管理")
@RestController
@RequestMapping("/admin/article")
public class ArticleAdminController {

    private final ArticleService articleService;

    public ArticleAdminController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @PostMapping
    @Operation(summary = "新增文章")
    @OperationLog(module = "文章管理", operation = "新增文章")
    public Result<Void> add(@Valid @RequestBody ArticleSaveDTO articleSaveDTO) {
        articleService.add(articleSaveDTO);
        return Result.success();
    }

    @PutMapping
    @Operation(summary = "修改文章")
    @OperationLog(module = "文章管理", operation = "修改文章")
    public Result<Void> update(@Valid @RequestBody ArticleSaveDTO articleSaveDTO) {
        articleService.update(articleSaveDTO);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除文章")
    @OperationLog(module = "文章管理", operation = "删除文章")
    public Result<Void> delete(@PathVariable Long id) {
        articleService.delete(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询文章详情")
    public Result<ArticleDetailVO> detail(@PathVariable Long id) {
        return Result.success(articleService.getById(id));
    }

    @GetMapping("/list")
    @Operation(summary = "分页查询文章列表")
    public Result<PageResult<ArticleAdminListVO>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(articleService.list(keyword, categoryId, status, pageNum, pageSize));
    }

    @PutMapping("/status")
    @Operation(summary = "修改文章状态")
    @OperationLog(module = "文章管理", operation = "修改文章状态")
    public Result<Void> updateStatus(@Valid @RequestBody ArticleStatusDTO articleStatusDTO) {
        articleService.updateStatus(articleStatusDTO);
        return Result.success();
    }
}
