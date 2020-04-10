package com.example.italker.mapper;

import com.example.italker.pojo.entity.Group;
import com.example.italker.pojo.entity.GroupMember;
import com.example.italker.pojo.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface GroupMapper {

    /**通过ID拿到群Model*/
    Group findById(String groupId);

    List<GroupMember> getMembers(Group group);

    void addMembers(GroupMember member);

    Group findByName(String toLowerCase);

    List<GroupMember> getMembersUser(User self);
    

    void saveGroup(Group group);

    void saveOwnerMember(GroupMember ownerMember);

    void saveMember(GroupMember member);

    GroupMember getMember(String userId, String groupId);

    List<Group> search(String name);
}
