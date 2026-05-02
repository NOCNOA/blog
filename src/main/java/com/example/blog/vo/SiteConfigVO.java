package com.example.blog.vo;

public class SiteConfigVO {

    private String siteName;
    private String siteLogo;
    private String siteDescription;
    private String siteNotice;
    private String footerInfo;
    private String githubUrl;
    private String giteeUrl;
    private String avatar;

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getSiteLogo() {
        return siteLogo;
    }

    public void setSiteLogo(String siteLogo) {
        this.siteLogo = siteLogo;
    }

    public String getSiteDescription() {
        return siteDescription;
    }

    public void setSiteDescription(String siteDescription) {
        this.siteDescription = siteDescription;
    }

    public String getSiteNotice() {
        return siteNotice;
    }

    public void setSiteNotice(String siteNotice) {
        this.siteNotice = siteNotice;
    }

    public String getFooterInfo() {
        return footerInfo;
    }

    public void setFooterInfo(String footerInfo) {
        this.footerInfo = footerInfo;
    }

    public String getGithubUrl() {
        return githubUrl;
    }

    public void setGithubUrl(String githubUrl) {
        this.githubUrl = githubUrl;
    }

    public String getGiteeUrl() {
        return giteeUrl;
    }

    public void setGiteeUrl(String giteeUrl) {
        this.giteeUrl = giteeUrl;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
