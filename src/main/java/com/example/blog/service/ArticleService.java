package com.example.blog.service;

import com.example.blog.common.result.PageResult;
import com.example.blog.dto.article.ArticleSaveDTO;
import com.example.blog.dto.article.ArticleStatusDTO;
import com.example.blog.vo.ArticleAdminListVO;
import com.example.blog.vo.ArticleArchiveVO;
import com.example.blog.vo.ArticleDetailVO;
import com.example.blog.vo.ArticlePortalDetailVO;
import com.example.blog.vo.ArticlePortalListVO;

import java.util.List;

public interface ArticleService {

    void add(ArticleSaveDTO articleSaveDTO);

    void update(ArticleSaveDTO articleSaveDTO);

    void delete(Long id);

    ArticleDetailVO getById(Long id);

    PageResult<ArticleAdminListVO> list(String keyword, Long categoryId, Integer status, Integer pageNum, Integer pageSize);

    void updateStatus(ArticleStatusDTO articleStatusDTO);

    PageResult<ArticlePortalListVO> listPortalArticles(String keyword, Long categoryId, Long tagId, Integer pageNum, Integer pageSize);

    ArticlePortalDetailVO getPortalArticleDetail(Long id);

    List<ArticleArchiveVO> listArchives();
}
