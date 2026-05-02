package com.example.blog.service;

import com.example.blog.dto.category.CategoryDTO;
import com.example.blog.vo.CategoryVO;

import java.util.List;

public interface CategoryService {

    void add(CategoryDTO categoryDTO);

    void update(CategoryDTO categoryDTO);

    void delete(Long id);

    CategoryVO getById(Long id);

    List<CategoryVO> list();
}
