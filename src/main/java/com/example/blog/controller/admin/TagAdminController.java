package com.example.blog.controller.admin;

import com.example.blog.annotation.OperationLog;
import com.example.blog.common.result.Result;
import com.example.blog.dto.tag.TagDTO;
import com.example.blog.service.TagService;
import com.example.blog.vo.TagVO;
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

@Tag(name = "后台-标签管理")
@RestController
@RequestMapping("/admin/tag")
public class TagAdminController {

    private final TagService tagService;

    public TagAdminController(TagService tagService) {
        this.tagService = tagService;
    }

    @PostMapping
    @Operation(summary = "新增标签")
    @OperationLog(module = "标签管理", operation = "新增标签")
    public Result<Void> add(@Valid @RequestBody TagDTO tagDTO) {
        tagService.add(tagDTO);
        return Result.success();
    }

    @PutMapping
    @Operation(summary = "修改标签")
    @OperationLog(module = "标签管理", operation = "修改标签")
    public Result<Void> update(@Valid @RequestBody TagDTO tagDTO) {
        tagService.update(tagDTO);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除标签")
    @OperationLog(module = "标签管理", operation = "删除标签")
    public Result<Void> delete(@PathVariable Long id) {
        tagService.delete(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询标签详情")
    public Result<TagVO> detail(@PathVariable Long id) {
        return Result.success(tagService.getById(id));
    }

    @GetMapping("/list")
    @Operation(summary = "查询标签列表")
    public Result<List<TagVO>> list() {
        return Result.success(tagService.list());
    }
}
