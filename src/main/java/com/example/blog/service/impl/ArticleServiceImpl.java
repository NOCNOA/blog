package com.example.blog.service.impl;

import com.example.blog.common.constant.ArticleStatusConstant;
import com.example.blog.common.exception.BusinessException;
import com.example.blog.common.result.PageResult;
import com.example.blog.common.result.ResultCode;
import com.example.blog.dto.article.ArticleSaveDTO;
import com.example.blog.dto.article.ArticleStatusDTO;
import com.example.blog.entity.Article;
import com.example.blog.entity.ArticleTag;
import com.example.blog.entity.Category;
import com.example.blog.entity.User;
import com.example.blog.mapper.ArticleMapper;
import com.example.blog.mapper.ArticleTagMapper;
import com.example.blog.mapper.CategoryMapper;
import com.example.blog.mapper.TagMapper;
import com.example.blog.mapper.UserMapper;
import com.example.blog.service.ArticleService;
import com.example.blog.util.LoginUserContext;
import com.example.blog.vo.ArticleAdminListVO;
import com.example.blog.vo.ArticleArchiveVO;
import com.example.blog.vo.ArticleDetailVO;
import com.example.blog.vo.ArticlePortalDetailVO;
import com.example.blog.vo.ArticlePortalListVO;
import com.example.blog.vo.TagVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ArticleServiceImpl implements ArticleService {

    private final ArticleMapper articleMapper;
    private final ArticleTagMapper articleTagMapper;
    private final CategoryMapper categoryMapper;
    private final TagMapper tagMapper;
    private final UserMapper userMapper;

    public ArticleServiceImpl(ArticleMapper articleMapper,
                              ArticleTagMapper articleTagMapper,
                              CategoryMapper categoryMapper,
                              TagMapper tagMapper,
                              UserMapper userMapper) {
        this.articleMapper = articleMapper;
        this.articleTagMapper = articleTagMapper;
        this.categoryMapper = categoryMapper;
        this.tagMapper = tagMapper;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional
    public void add(ArticleSaveDTO articleSaveDTO) {
        validateCategory(articleSaveDTO.getCategoryId());
        Article article = buildArticle(articleSaveDTO, new Article());
        Long currentUserId = LoginUserContext.getUserId();
        if (currentUserId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED.getCode(), "未登录或登录已失效");
        }
        article.setAuthorId(currentUserId);
        article.setViewCount(0L);
        articleMapper.insert(article);
        saveArticleTags(article.getId(), articleSaveDTO.getTagIdList());
    }

    @Override
    @Transactional
    public void update(ArticleSaveDTO articleSaveDTO) {
        if (articleSaveDTO.getId() == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "文章ID不能为空");
        }
        Article existing = articleMapper.selectById(articleSaveDTO.getId());
        if (existing == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "文章不存在");
        }
        validateCategory(articleSaveDTO.getCategoryId());
        Article article = buildArticle(articleSaveDTO, existing);
        articleMapper.updateById(article);
        articleTagMapper.deleteByArticleId(article.getId());
        saveArticleTags(article.getId(), articleSaveDTO.getTagIdList());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Article article = articleMapper.selectById(id);
        if (article == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "文章不存在");
        }
        articleTagMapper.deleteByArticleId(id);
        articleMapper.deleteById(id);
    }

    @Override
    public ArticleDetailVO getById(Long id) {
        Article article = articleMapper.selectById(id);
        if (article == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "文章不存在");
        }

        ArticleDetailVO vo = new ArticleDetailVO();
        vo.setId(article.getId());
        vo.setTitle(article.getTitle());
        vo.setSummary(article.getSummary());
        vo.setContent(article.getContent());
        vo.setCoverImage(article.getCoverImage());
        vo.setCategoryId(article.getCategoryId());
        vo.setCategoryName(resolveCategoryName(article.getCategoryId()));
        vo.setAuthorId(article.getAuthorId());
        vo.setAuthorName(resolveAuthorName(article.getAuthorId()));
        vo.setStatus(article.getStatus());
        vo.setIsTop(article.getIsTop());
        vo.setViewCount(article.getViewCount());
        vo.setPublishTime(article.getPublishTime());
        vo.setTagIdList(articleTagMapper.selectTagIdsByArticleId(id));
        vo.setTags(tagMapper.selectByArticleId(id));
        return vo;
    }

    @Override
    public PageResult<ArticleAdminListVO> list(String keyword, Long categoryId, Integer status, Integer pageNum, Integer pageSize) {
        int validPageNum = normalizePageNum(pageNum);
        int validPageSize = normalizePageSize(pageSize);
        int offset = (validPageNum - 1) * validPageSize;
        long total = articleMapper.countAdminList(keyword, categoryId, status);
        List<ArticleAdminListVO> records = articleMapper.selectAdminList(keyword, categoryId, status, offset, validPageSize);
        attachTagsToAdminRecords(records);
        return new PageResult<>(total, validPageNum, validPageSize, records);
    }

    @Override
    public void updateStatus(ArticleStatusDTO articleStatusDTO) {
        Article article = articleMapper.selectById(articleStatusDTO.getId());
        if (article == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "文章不存在");
        }
        LocalDateTime publishTime = article.getPublishTime();
        if (articleStatusDTO.getStatus() != null
                && articleStatusDTO.getStatus() == ArticleStatusConstant.PUBLISHED
                && publishTime == null) {
            publishTime = LocalDateTime.now();
        }
        articleMapper.updateStatus(articleStatusDTO.getId(), articleStatusDTO.getStatus(), publishTime);
    }

    @Override
    public PageResult<ArticlePortalListVO> listPortalArticles(String keyword, Long categoryId, Long tagId, Integer pageNum, Integer pageSize) {
        int validPageNum = normalizePageNum(pageNum);
        int validPageSize = normalizePageSize(pageSize);
        int offset = (validPageNum - 1) * validPageSize;
        long total = articleMapper.countPortalList(keyword, categoryId, tagId);
        List<ArticlePortalListVO> records = articleMapper.selectPortalList(keyword, categoryId, tagId, offset, validPageSize);
        attachTagsToPortalRecords(records);
        return new PageResult<>(total, validPageNum, validPageSize, records);
    }

    @Override
    @Transactional
    public ArticlePortalDetailVO getPortalArticleDetail(Long id) {
        ArticlePortalDetailVO detail = articleMapper.selectPortalDetailById(id);
        if (detail == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "文章不存在或未发布");
        }
        articleMapper.increaseViewCount(id);
        detail.setViewCount(detail.getViewCount() == null ? 1L : detail.getViewCount() + 1);
        detail.setTags(tagMapper.selectByArticleId(id));
        return detail;
    }

    @Override
    public List<ArticleArchiveVO> listArchives() {
        return articleMapper.selectArchiveList();
    }

    private Article buildArticle(ArticleSaveDTO dto, Article article) {
        article.setTitle(dto.getTitle());
        article.setSummary(dto.getSummary());
        article.setContent(dto.getContent());
        article.setCoverImage(dto.getCoverImage());
        article.setCategoryId(dto.getCategoryId());
        article.setStatus(dto.getStatus());
        article.setIsTop(dto.getIsTop());
        if (dto.getStatus() != null
                && dto.getStatus() == ArticleStatusConstant.PUBLISHED
                && article.getPublishTime() == null) {
            article.setPublishTime(LocalDateTime.now());
        }
        if (dto.getStatus() != null
                && dto.getStatus() != ArticleStatusConstant.PUBLISHED
                && article.getPublishTime() == null) {
            article.setPublishTime(null);
        }
        return article;
    }

    private void validateCategory(Long categoryId) {
        if (categoryMapper.selectById(categoryId) == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "分类不存在");
        }
    }

    private void saveArticleTags(Long articleId, List<Long> tagIdList) {
        if (tagIdList == null || tagIdList.isEmpty()) {
            return;
        }
        List<ArticleTag> relations = new ArrayList<>();
        for (Long tagId : tagIdList) {
            ArticleTag relation = new ArticleTag();
            relation.setArticleId(articleId);
            relation.setTagId(tagId);
            relations.add(relation);
        }
        articleTagMapper.batchInsert(relations);
    }

    private int normalizePageNum(Integer pageNum) {
        return pageNum == null || pageNum < 1 ? 1 : pageNum;
    }

    private int normalizePageSize(Integer pageSize) {
        if (pageSize == null || pageSize < 1) {
            return 10;
        }
        return Math.min(pageSize, 100);
    }

    private void attachTagsToAdminRecords(List<ArticleAdminListVO> records) {
        if (records == null || records.isEmpty()) {
            return;
        }
        Map<Long, List<TagVO>> tagMap = buildTagMap(extractArticleIdsFromAdmin(records));
        for (ArticleAdminListVO record : records) {
            record.setTags(tagMap.getOrDefault(record.getId(), Collections.emptyList()));
        }
    }

    private void attachTagsToPortalRecords(List<ArticlePortalListVO> records) {
        if (records == null || records.isEmpty()) {
            return;
        }
        Map<Long, List<TagVO>> tagMap = buildTagMap(extractArticleIdsFromPortal(records));
        for (ArticlePortalListVO record : records) {
            record.setTags(tagMap.getOrDefault(record.getId(), Collections.emptyList()));
        }
    }

    private Map<Long, List<TagVO>> buildTagMap(List<Long> articleIds) {
        if (articleIds == null || articleIds.isEmpty()) {
            return Collections.emptyMap();
        }
        List<ArticleTag> relations = articleTagMapper.selectByArticleIds(articleIds);
        if (relations == null || relations.isEmpty()) {
            return Collections.emptyMap();
        }

        List<Long> tagIds = new ArrayList<>();
        Map<Long, List<Long>> articleTagIdsMap = new LinkedHashMap<>();
        for (ArticleTag relation : relations) {
            articleTagIdsMap.computeIfAbsent(relation.getArticleId(), key -> new ArrayList<>()).add(relation.getTagId());
            if (!tagIds.contains(relation.getTagId())) {
                tagIds.add(relation.getTagId());
            }
        }

        List<TagVO> tags = tagMapper.selectByIds(tagIds);
        Map<Long, TagVO> tagLookup = new LinkedHashMap<>();
        for (TagVO tag : tags) {
            tagLookup.put(tag.getId(), tag);
        }

        Map<Long, List<TagVO>> result = new LinkedHashMap<>();
        for (Map.Entry<Long, List<Long>> entry : articleTagIdsMap.entrySet()) {
            List<TagVO> articleTags = new ArrayList<>();
            for (Long tagId : entry.getValue()) {
                TagVO tag = tagLookup.get(tagId);
                if (tag != null) {
                    articleTags.add(tag);
                }
            }
            result.put(entry.getKey(), articleTags);
        }
        return result;
    }

    private List<Long> extractArticleIdsFromAdmin(List<ArticleAdminListVO> records) {
        List<Long> ids = new ArrayList<>();
        for (ArticleAdminListVO record : records) {
            ids.add(record.getId());
        }
        return ids;
    }

    private List<Long> extractArticleIdsFromPortal(List<ArticlePortalListVO> records) {
        List<Long> ids = new ArrayList<>();
        for (ArticlePortalListVO record : records) {
            ids.add(record.getId());
        }
        return ids;
    }

    private String resolveCategoryName(Long categoryId) {
        if (categoryId == null) {
            return null;
        }
        Category category = categoryMapper.selectById(categoryId);
        return category == null ? null : category.getName();
    }

    private String resolveAuthorName(Long authorId) {
        if (authorId == null) {
            return null;
        }
        User user = userMapper.selectById(authorId);
        return user == null ? null : user.getNickname();
    }
}
