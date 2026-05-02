package com.example.blog.controller.portal;

import com.example.blog.common.result.PageResult;
import com.example.blog.common.result.Result;
import com.example.blog.service.ArticleService;
import com.example.blog.vo.ArticleArchiveVO;
import com.example.blog.vo.ArticlePortalDetailVO;
import com.example.blog.vo.ArticlePortalListVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "前台-文章展示")
@RestController
@RequestMapping("/article")
public class ArticleController {

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping("/list")
    @Operation(summary = "分页查询前台文章列表")
    public Result<PageResult<ArticlePortalListVO>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long tagId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(articleService.listPortalArticles(keyword, categoryId, tagId, pageNum, pageSize));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询前台文章详情")
    public Result<ArticlePortalDetailVO> detail(@PathVariable Long id) {
        return Result.success(articleService.getPortalArticleDetail(id));
    }

    @GetMapping("/archive")
    @Operation(summary = "查询文章归档列表")
    public Result<List<ArticleArchiveVO>> archive() {
        return Result.success(articleService.listArchives());
    }
}
