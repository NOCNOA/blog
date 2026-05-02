package com.example.blog.mapper;

import com.example.blog.entity.Tag;
import com.example.blog.vo.TagVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TagMapper {

    List<Tag> selectAll();

    Tag selectById(@Param("id") Long id);

    Tag selectByName(@Param("name") String name);

    int insert(Tag tag);

    int updateById(Tag tag);

    int deleteById(@Param("id") Long id);

    int deleteArticleRelationByTagId(@Param("tagId") Long tagId);

    List<TagVO> selectByArticleId(@Param("articleId") Long articleId);

    List<TagVO> selectByIds(@Param("tagIds") List<Long> tagIds);

    long countAll();
}
