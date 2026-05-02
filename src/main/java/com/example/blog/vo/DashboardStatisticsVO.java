package com.example.blog.vo;

public class DashboardStatisticsVO {

    private Long articleCount;
    private Long publishedArticleCount;
    private Long draftArticleCount;
    private Long categoryCount;
    private Long tagCount;

    public Long getArticleCount() {
        return articleCount;
    }

    public void setArticleCount(Long articleCount) {
        this.articleCount = articleCount;
    }

    public Long getPublishedArticleCount() {
        return publishedArticleCount;
    }

    public void setPublishedArticleCount(Long publishedArticleCount) {
        this.publishedArticleCount = publishedArticleCount;
    }

    public Long getDraftArticleCount() {
        return draftArticleCount;
    }

    public void setDraftArticleCount(Long draftArticleCount) {
        this.draftArticleCount = draftArticleCount;
    }

    public Long getCategoryCount() {
        return categoryCount;
    }

    public void setCategoryCount(Long categoryCount) {
        this.categoryCount = categoryCount;
    }

    public Long getTagCount() {
        return tagCount;
    }

    public void setTagCount(Long tagCount) {
        this.tagCount = tagCount;
    }
}
