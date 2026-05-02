package com.example.blog.service.impl;

import com.example.blog.mapper.ArticleMapper;
import com.example.blog.mapper.CategoryMapper;
import com.example.blog.mapper.TagMapper;
import com.example.blog.service.DashboardService;
import com.example.blog.vo.DashboardStatisticsVO;
import org.springframework.stereotype.Service;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final ArticleMapper articleMapper;
    private final CategoryMapper categoryMapper;
    private final TagMapper tagMapper;

    public DashboardServiceImpl(ArticleMapper articleMapper, CategoryMapper categoryMapper, TagMapper tagMapper) {
        this.articleMapper = articleMapper;
        this.categoryMapper = categoryMapper;
        this.tagMapper = tagMapper;
    }

    @Override
    public DashboardStatisticsVO getStatistics() {
        DashboardStatisticsVO vo = new DashboardStatisticsVO();
        vo.setArticleCount(articleMapper.countAll());
        vo.setPublishedArticleCount(articleMapper.countByStatus(1));
        vo.setDraftArticleCount(articleMapper.countByStatus(0));
        vo.setCategoryCount(categoryMapper.countAll());
        vo.setTagCount(tagMapper.countAll());
        return vo;
    }
}
