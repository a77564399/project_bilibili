package com.project.bilibili.dao;

import com.alibaba.fastjson.JSONObject;
import com.project.bilibili.domain.RefreshTokenDetail;
import com.project.bilibili.domain.User;
import com.project.bilibili.domain.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.*;

@Mapper
public interface UserDao {

    User getUserByPhone(String phone);

    Integer addUser(User user);

    Integer addUserInfo(UserInfo userInfo);

    User getUserById(Long id);

    UserInfo getUserInfoById(Long userId);

    Integer updateUserInfos(UserInfo userInfo);

    Integer updateUsers(User user);

    List<UserInfo> getUserInfoByUserIds(Set<Long> userIdList);

//  此处JSONObject类改为map父类，因为可以直接在xml中写Java.map
    Integer getCountUserInfos(Map<String, Object> jsonObject);

    List<UserInfo> pageListUserInfos(JSONObject jsonObject);

    Integer deleteRefreshToken(@Param("refreshToken") String refreshToken, @Param("userId") Long userId);

    Integer addRefreshToken(@Param("refreshToken") String refreshToken,
                            @Param("userId") Long userId,
                            @Param("createTime") Date createTime);

    RefreshTokenDetail getRefreshTokenDetail(String refreshToken);
}
