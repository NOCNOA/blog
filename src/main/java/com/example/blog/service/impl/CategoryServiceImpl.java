package com.example.blog.service.impl;

import com.example.blog.common.exception.BusinessException;
import com.example.blog.common.result.ResultCode;
import com.example.blog.dto.category.CategoryDTO;
import com.example.blog.entity.Category;
import com.example.blog.mapper.CategoryMapper;
import com.example.blog.service.CategoryService;
import com.example.blog.vo.CategoryVO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;

    public CategoryServiceImpl(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    @Override
    public void add(CategoryDTO categoryDTO) {
        if (categoryMapper.selectByName(categoryDTO.getName()) != null) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "分类名称已存在");
        }
        Category category = new Category();
        category.setName(categoryDTO.getName());
        category.setDescription(categoryDTO.getDescription());
        category.setSort(categoryDTO.getSort());
        categoryMapper.insert(category);
    }

    @Override
    public void update(CategoryDTO categoryDTO) {
        if (categoryDTO.getId() == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "分类ID不能为空");
        }
        Category existing = categoryMapper.selectById(categoryDTO.getId());
        if (existing == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "分类不存在");
        }
        Category sameName = categoryMapper.selectByName(categoryDTO.getName());
        if (sameName != null && !sameName.getId().equals(categoryDTO.getId())) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "分类名称已存在");
        }
        Category category = new Category();
        category.setId(categoryDTO.getId());
        category.setName(categoryDTO.getName());
        category.setDescription(categoryDTO.getDescription());
        category.setSort(categoryDTO.getSort());
        categoryMapper.updateById(category);
    }

    @Override
    public void delete(Long id) {
        Category existing = categoryMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "分类不存在");
        }
        if (categoryMapper.countArticleByCategoryId(id) > 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "该分类下还有文章，不能删除");
        }
        categoryMapper.deleteById(id);
    }

    @Override
    public CategoryVO getById(Long id) {
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "分类不存在");
        }
        return toVO(category);
    }

    @Override
    public List<CategoryVO> list() {
        List<Category> categories = categoryMapper.selectAll();
        List<CategoryVO> result = new ArrayList<>();
        for (Category category : categories) {
            result.add(toVO(category));
        }
        return result;
    }

    private CategoryVO toVO(Category category) {
        CategoryVO vo = new CategoryVO();
        vo.setId(category.getId());
        vo.setName(category.getName());
        vo.setDescription(category.getDescription());
        vo.setSort(category.getSort());
        return vo;
    }
}
