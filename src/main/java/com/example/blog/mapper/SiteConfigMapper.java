package com.example.blog.mapper;

import com.example.blog.entity.SiteConfig;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SiteConfigMapper {

    SiteConfig selectCurrent();

    int updateCurrent(SiteConfig siteConfig);

    int insert(SiteConfig siteConfig);
}
