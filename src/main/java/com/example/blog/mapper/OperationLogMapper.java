package com.example.blog.mapper;

import com.example.blog.entity.OperationLogEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OperationLogMapper {

    int insert(OperationLogEntity operationLog);
}
