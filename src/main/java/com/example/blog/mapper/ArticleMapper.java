package com.example.blog.mapper;

import com.example.blog.entity.Article;
import com.example.blog.vo.ArticleAdminListVO;
import com.example.blog.vo.ArticleArchiveVO;
import com.example.blog.vo.ArticlePortalDetailVO;
import com.example.blog.vo.ArticlePortalListVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ArticleMapper {

    List<Article> selectPublishedList();

    Article selectById(@Param("id") Long id);

    int insert(Article article);

    int updateById(Article article);

    int deleteById(@Param("id") Long id);

    List<ArticleAdminListVO> selectAdminList(@Param("keyword") String keyword,
                                             @Param("categoryId") Long categoryId,
                                             @Param("status") Integer status,
                                             @Param("offset") Integer offset,
                                             @Param("pageSize") Integer pageSize);

    long countAdminList(@Param("keyword") String keyword,
                        @Param("categoryId") Long categoryId,
                        @Param("status") Integer status);

    int updateStatus(@Param("id") Long id,
                     @Param("status") Integer status,
                     @Param("publishTime") LocalDateTime publishTime);

    List<ArticlePortalListVO> selectPortalList(@Param("keyword") String keyword,
                                               @Param("categoryId") Long categoryId,
                                               @Param("tagId") Long tagId,
                                               @Param("offset") Integer offset,
                                               @Param("pageSize") Integer pageSize);

    long countPortalList(@Param("keyword") String keyword,
                         @Param("categoryId") Long categoryId,
                         @Param("tagId") Long tagId);

    ArticlePortalDetailVO selectPortalDetailById(@Param("id") Long id);

    int increaseViewCount(@Param("id") Long id);

    List<ArticleArchiveVO> selectArchiveList();

    long countAll();

    long countByStatus(@Param("status") Integer status);
}
