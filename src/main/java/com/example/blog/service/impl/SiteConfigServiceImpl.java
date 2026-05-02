package com.example.blog.service.impl;

import com.example.blog.dto.site.SiteConfigDTO;
import com.example.blog.entity.SiteConfig;
import com.example.blog.mapper.SiteConfigMapper;
import com.example.blog.service.SiteConfigService;
import com.example.blog.vo.SiteConfigVO;
import org.springframework.stereotype.Service;

@Service
public class SiteConfigServiceImpl implements SiteConfigService {

    private final SiteConfigMapper siteConfigMapper;

    public SiteConfigServiceImpl(SiteConfigMapper siteConfigMapper) {
        this.siteConfigMapper = siteConfigMapper;
    }

    @Override
    public SiteConfigVO getCurrent() {
        SiteConfig siteConfig = siteConfigMapper.selectCurrent();
        if (siteConfig == null) {
            return new SiteConfigVO();
        }
        return toVO(siteConfig);
    }

    @Override
    public void updateCurrent(SiteConfigDTO siteConfigDTO) {
        SiteConfig current = siteConfigMapper.selectCurrent();
        SiteConfig siteConfig = toEntity(siteConfigDTO);
        if (current == null) {
            siteConfigMapper.insert(siteConfig);
            return;
        }
        siteConfig.setId(current.getId());
        siteConfigMapper.updateCurrent(siteConfig);
    }

    private SiteConfig toEntity(SiteConfigDTO dto) {
        SiteConfig siteConfig = new SiteConfig();
        siteConfig.setSiteName(dto.getSiteName());
        siteConfig.setSiteLogo(dto.getSiteLogo());
        siteConfig.setSiteDescription(dto.getSiteDescription());
        siteConfig.setSiteNotice(dto.getSiteNotice());
        siteConfig.setFooterInfo(dto.getFooterInfo());
        siteConfig.setGithubUrl(dto.getGithubUrl());
        siteConfig.setGiteeUrl(dto.getGiteeUrl());
        siteConfig.setAvatar(dto.getAvatar());
        return siteConfig;
    }

    private SiteConfigVO toVO(SiteConfig siteConfig) {
        SiteConfigVO vo = new SiteConfigVO();
        vo.setSiteName(siteConfig.getSiteName());
        vo.setSiteLogo(siteConfig.getSiteLogo());
        vo.setSiteDescription(siteConfig.getSiteDescription());
        vo.setSiteNotice(siteConfig.getSiteNotice());
        vo.setFooterInfo(siteConfig.getFooterInfo());
        vo.setGithubUrl(siteConfig.getGithubUrl());
        vo.setGiteeUrl(siteConfig.getGiteeUrl());
        vo.setAvatar(siteConfig.getAvatar());
        return vo;
    }
}
