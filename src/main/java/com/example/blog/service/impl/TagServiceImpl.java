package com.example.blog.service.impl;

import com.example.blog.common.exception.BusinessException;
import com.example.blog.common.result.ResultCode;
import com.example.blog.dto.tag.TagDTO;
import com.example.blog.entity.Tag;
import com.example.blog.mapper.TagMapper;
import com.example.blog.service.TagService;
import com.example.blog.vo.TagVO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TagServiceImpl implements TagService {

    private final TagMapper tagMapper;

    public TagServiceImpl(TagMapper tagMapper) {
        this.tagMapper = tagMapper;
    }

    @Override
    public void add(TagDTO tagDTO) {
        if (tagMapper.selectByName(tagDTO.getName()) != null) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "标签名称已存在");
        }
        Tag tag = new Tag();
        tag.setName(tagDTO.getName());
        tagMapper.insert(tag);
    }

    @Override
    public void update(TagDTO tagDTO) {
        if (tagDTO.getId() == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "标签ID不能为空");
        }
        Tag existing = tagMapper.selectById(tagDTO.getId());
        if (existing == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "标签不存在");
        }
        Tag sameName = tagMapper.selectByName(tagDTO.getName());
        if (sameName != null && !sameName.getId().equals(tagDTO.getId())) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "标签名称已存在");
        }
        Tag tag = new Tag();
        tag.setId(tagDTO.getId());
        tag.setName(tagDTO.getName());
        tagMapper.updateById(tag);
    }

    @Override
    public void delete(Long id) {
        Tag existing = tagMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "标签不存在");
        }
        tagMapper.deleteArticleRelationByTagId(id);
        tagMapper.deleteById(id);
    }

    @Override
    public TagVO getById(Long id) {
        Tag tag = tagMapper.selectById(id);
        if (tag == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "标签不存在");
        }
        return toVO(tag);
    }

    @Override
    public List<TagVO> list() {
        List<Tag> tags = tagMapper.selectAll();
        List<TagVO> result = new ArrayList<>();
        for (Tag tag : tags) {
            result.add(toVO(tag));
        }
        return result;
    }

    private TagVO toVO(Tag tag) {
        TagVO vo = new TagVO();
        vo.setId(tag.getId());
        vo.setName(tag.getName());
        return vo;
    }
}
