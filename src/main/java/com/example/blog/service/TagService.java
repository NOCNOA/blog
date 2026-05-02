package com.example.blog.service;

import com.example.blog.dto.tag.TagDTO;
import com.example.blog.vo.TagVO;

import java.util.List;

public interface TagService {

    void add(TagDTO tagDTO);

    void update(TagDTO tagDTO);

    void delete(Long id);

    TagVO getById(Long id);

    List<TagVO> list();
}
