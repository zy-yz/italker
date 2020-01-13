package com.example.italker.mapper;

import com.example.italker.pojo.entity.Group;
import com.example.italker.pojo.entity.GroupMember;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface GroupMapper {

    /**通过ID拿到群Model*/
    Group findById(String groupId);

    List<GroupMember> getMembers(Group group);
}
