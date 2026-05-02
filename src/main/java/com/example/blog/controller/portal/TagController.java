package com.example.blog.controller.portal;

import com.example.blog.common.result.Result;
import com.example.blog.service.TagService;
import com.example.blog.vo.TagVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "前台-标签展示")
@RestController
@RequestMapping("/tag")
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping("/list")
    @Operation(summary = "查询前台标签列表")
    public Result<List<TagVO>> list() {
        return Result.success(tagService.list());
    }
}
