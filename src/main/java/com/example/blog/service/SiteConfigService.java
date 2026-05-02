package com.example.blog.service;

import com.example.blog.dto.site.SiteConfigDTO;
import com.example.blog.vo.SiteConfigVO;

public interface SiteConfigService {

    SiteConfigVO getCurrent();

    void updateCurrent(SiteConfigDTO siteConfigDTO);
}
