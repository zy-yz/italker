package com.example.italker.service;

import com.example.italker.mapper.GroupMapper;
import com.example.italker.pojo.entity.Apply;
import com.example.italker.pojo.entity.Group;
import com.example.italker.pojo.entity.GroupMember;
import com.example.italker.pojo.entity.User;
import com.example.italker.pojo.view.group.GroupApplyModel;
import com.example.italker.pojo.view.group.GroupCreateModel;
import com.example.italker.pojo.view.group.GroupMemberUpdateModel;
import com.google.common.base.Strings;
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

    //查询一个群,同时该User必须为群的成员，否则返回null
    public Group findById(User user, String receiverId) {

        GroupMember member = getMember(user.getId(), receiverId);

        if(member != null){
            return member.getGroup();
        }

        return null;
    }

    public Group findByName(String name) {
        return groupMapper.findByName(name.toLowerCase());
    }

    //获取一个群的所有成员
    public Set<GroupMember> getMembers(Group group) {
        @SuppressWarnings("unchecked")
        List<GroupMember> members = groupMapper.getMembers(group.getId());
        return new HashSet<>(members);
    }

    //查询一个人加入的所有群
    public Set<GroupMember> getMembers(User self) {
        @SuppressWarnings("unchecked")
        Group group = groupMapper.findById(self.getId());
        List<GroupMember> members = null;
        if (group==null){
            return (Set<GroupMember>) members;
        }
        members = groupMapper.getMembersUser(self.getId(),group.getId());
        return new HashSet<>(members);
    }

    public Group create(User creator, GroupCreateModel model, List<User> users) {
        Group group = new Group(creator,model);

        Integer id =  groupMapper.saveGroup(group);


        GroupMember ownerMember = new GroupMember(creator, group);

        //设置创建者超级管理员权限
        ownerMember.setPermissionType(GroupMember.PERMISSION_TYPE_ADMIN_SU);


        groupMapper.saveMember(ownerMember);

        for (User user : users) {
            GroupMember member = new GroupMember(user, group);
            //保存，还没有提交到数据库
           groupMapper.saveMember(member);
        }
        return group;

    }

    //获取一个群的成员
    public GroupMember getMember(String userId, String groupId) {
       return groupMapper.getMember(userId,groupId);
    }

    //查询
    @SuppressWarnings("unchecked")
    public List<Group> search(String name) {
        if (Strings.isNullOrEmpty(name)) {
            name = "";
        }
        List<Group>  list =  groupMapper.search(name);

        return list;
    }







    public Set<GroupMember> addMembers(Group group, List<User> insertUsers) {
        Set<GroupMember> members = new HashSet<>();
        for (User user : insertUsers) {
            GroupMember member = new GroupMember(user, group);
            // 保存，并没有提交到数据库
            groupMapper.saveMember(member);
            members.add(member);
        }
        return members;
    }

    public Group getGroup(String id) {
        return groupMapper.getGroup(id);
    }

    /**
     * @Description     修改群成员的信息
     * @param [memberId, model, isAmind]
     * @return com.example.italker.pojo.entity.GroupMember
     */
    public GroupMember updateMember(String memberId, GroupMemberUpdateModel model, boolean isAdmin) {

        GroupMember member = groupMapper.getMemberById(memberId,model.getGroupId());

        member.setAlias(model.getAlias());
        //1.在参数中修改了权限 同时你是普通权限 同时申请接口的用户有对应的权限 满足三者才能修改权限
        if (model.isAdmin() && member.getPermissionType() == GroupMember.NOTIFY_LEVEL_NONE && isAdmin) {
            member.setPermissionType(GroupMember.PERMISSION_TYPE_ADMIN);
        }
        groupMapper.updateMember(member);
        return groupMapper.getMemberById(memberId,model.getGroupId());


    }

    public Apply joinApply(String groupId, User self, GroupApplyModel model) {
        Apply apply = new Apply();
        apply.setApplicant(self);
        apply.setDescription(Strings.isNullOrEmpty(model.getDesciption()) ? "我先加入群聊!!!" : model.getDesciption());
        apply.setAttach(Strings.isNullOrEmpty(model.getAttach()) ? "" : model.getAttach());
        apply.setType(Apply.TYPE_ADD_GROUP);
        apply.setTargetId(groupId);

        groupMapper.insertApply(apply);

        return apply;
    }
}
