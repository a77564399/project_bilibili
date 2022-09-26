package com.project.bilibili.service;

import com.project.bilibili.dao.FollowingGroupDao;
import com.project.bilibili.domain.FollowingGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FollowingGroupService {
    @Autowired
    private FollowingGroupDao followingGroupDao;

    public FollowingGroup getByType(String type)
    {
        return followingGroupDao.getByType(type);
    }

    public FollowingGroup getById(Long id)
    {
        return followingGroupDao.getById(id);
    }

    public List<FollowingGroup> getByUserId(Long userId) {
        return followingGroupDao.getByUserId(userId);
    }

    public void addUserFollowingGroup(FollowingGroup followingGroup) {
        followingGroupDao.addUserFollowingGroup(followingGroup);
    }


    public List<FollowingGroup> getUserFollowingGroup(Long userId) {
        return followingGroupDao.getUserFollowingGroup(userId);
    }
}
