package com.example.italker.service;

import com.example.italker.mapper.GroupMapper;
import com.example.italker.pojo.entity.Group;
import com.example.italker.pojo.entity.GroupMember;
import com.example.italker.pojo.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class GroupService {

    @Autowired
    private GroupMapper groupMapper;

    /**通过ID拿群Model*/
    public Group findById(String groupId) {
        return groupMapper.findById(groupId);
    }

    public Group findById(User user, String groupId) {
        return groupMapper.findById(groupId);
    }

    //获取一个群的所有成员
    public Set<GroupMember> getMembers(Group group) {
        @SuppressWarnings("unchecked")
        List<GroupMember> members = groupMapper.getMembers(group);
        return new HashSet<>(members);
    }
}
