package com.example.blog.mapper;

import com.example.blog.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

    User selectByUsername(@Param("username") String username);

    User selectById(@Param("id") Long id);

    int updateProfileById(@Param("id") Long id,
                          @Param("nickname") String nickname,
                          @Param("avatar") String avatar,
                          @Param("email") String email);

    int updatePasswordById(@Param("id") Long id, @Param("password") String password);
}
