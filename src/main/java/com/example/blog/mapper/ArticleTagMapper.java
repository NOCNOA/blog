package com.example.blog.mapper;

import com.example.blog.entity.ArticleTag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ArticleTagMapper {

    List<ArticleTag> selectByArticleId(Long articleId);

    List<ArticleTag> selectByArticleIds(@Param("articleIds") List<Long> articleIds);

    List<Long> selectTagIdsByArticleId(@Param("articleId") Long articleId);

    int deleteByArticleId(@Param("articleId") Long articleId);

    int batchInsert(@Param("list") List<ArticleTag> list);
}
