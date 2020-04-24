package com.example.italker.controller;


import com.alibaba.fastjson.JSONObject;
import com.example.italker.pojo.card.ApplyCard;
import com.example.italker.pojo.card.GroupCard;
import com.example.italker.pojo.card.GroupMemberCard;
import com.example.italker.pojo.entity.Group;
import com.example.italker.pojo.entity.GroupMember;
import com.example.italker.pojo.entity.User;
import com.example.italker.pojo.view.base.ResponseModel;
import com.example.italker.pojo.view.group.GroupCreateModel;
import com.example.italker.pojo.view.group.GroupMemberAddModel;
import com.example.italker.pojo.view.group.GroupMemberUpdateModel;
import com.example.italker.provider.LocalDateTimeConverter;
import com.example.italker.service.GroupService;
import com.example.italker.service.PushService;
import com.example.italker.service.UserService;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/** 群组的接口的入口
 * @Description:
 * @Email: 1090712762@qq.com
 * @Author: Rattan Pepper
 * @Date: 2020/1/19
 */
@RestController
@RequestMapping("/group")
public class GroupController {

    @Autowired
    private GroupService groupService;

    @Autowired
    private UserService userService;

    @Autowired
    private PushService pushservice;

    @PostMapping(value = "/create")
    @ApiOperation(value = "创建群")
    public ResponseModel<GroupCard> create(@RequestBody GroupCreateModel model,
                                           HttpServletRequest request) {

        if (!GroupCreateModel.check(model)) {
            return ResponseModel.buildParameterError();
        }
        //创建者
        User creator = (User)request.getAttribute("aself");
         //创建者并不在列表中
        model.getUsers().remove(creator.getId());
        if (model.getUsers().size() == 0) {
            return ResponseModel.buildParameterError();
        }
        // 检查是否已有
        if(groupService.findByName(model.getName()) != null){
            return ResponseModel.buildHaveNameError();
        }
        List<User> users = new ArrayList<>();
        for (Object s : model.getUsers()) {
            Gson gson = new Gson();
            String json = gson.toJson(s);
            JSONObject jsonObject = JSONObject.parseObject(json);
            String r = jsonObject.getString("id");
            User user = userService.findById(r);
            if (user == null) {
                continue;
            }
            users.add(user);
        }
        // 没有一个成员
        if (users.size() == 0) {
            return ResponseModel.buildParameterError();
        }

        Group group = groupService.create(creator,model,users);
        if (group == null) {
            // 服务器异常
            return ResponseModel.buildServiceError();
        }
        // 拿管理员的信息（自己的信息）
        GroupMember creatorMember =groupService.getMember(creator.getId(),group.getId());
        if (creatorMember == null) {
            // 服务器异常
            return ResponseModel.buildServiceError();
        }

        //拿到群的成员,给所有的群成员发送信息，已经被添加到群的信息
        Set<GroupMember> members = groupService.getMembers(group);
        if(members == null){
            //服务器异常
            return ResponseModel.buildServiceError();
        }
        /**这里是不能注释的*/
        members = members.stream()
                .filter(groupMember -> !groupMember.getId().equalsIgnoreCase(creatorMember.getId()))
                .collect(Collectors.toSet());

        //开始发起推送
      pushservice.pushJoinGroup(members);

        creatorMember.setGroup(group);

        return ResponseModel.buildOk(new GroupCard(creatorMember));

    }


    @GetMapping(value = "/search/{name}")
    @ApiOperation(value = "查找群，没有传递参数就搜索最近所有的群")
    public ResponseModel<List<GroupCard>> search(@PathVariable String name,
                                                 HttpServletRequest request){
        User self = (User)request.getAttribute("aself");
        List<Group> groups = groupService.search(name);
        if(groups != null && groups.size() > 0){
            List<GroupCard> groupCards = groups.stream()
                    .map(group -> {
                        GroupMember member = groupService.getMember(self.getId(),group.getId());
                        return new GroupCard(group,member);
                    }).collect(Collectors.toList());
            return ResponseModel.buildOk(groupCards);
        }
        return ResponseModel.buildOk();
    }


    @GetMapping(value = "/list/{date}")
    @ApiOperation(value = "拉取自己当前的群的列表" +
            "时间字段不传递，则返回全部当前的群列表；有时间，则返回这个时间之后的加入的群")
    public ResponseModel<List<GroupCard>> list(@DefaultValue("") @PathVariable("date") String dateStr,
                                               HttpServletRequest request){

        User self = (User)request.getAttribute("aself");
        //拿时间
        LocalDateTime dateTime = null;
        if(!Strings.isNullOrEmpty(dateStr)){
            try {
                dateTime = LocalDateTime.parse(dateStr, LocalDateTimeConverter.FORMATTER);
            }catch (Exception e){
                return null;
            }
        }

        Set<GroupMember> members = groupService.getMembers(self);
        if (members == null || members.size() == 0) {
            return ResponseModel.buildOk();
        }
        final LocalDateTime finalDateTime = dateTime;
        List<GroupCard> groupCards = members.stream()
                //时间为null则不作限制
                .filter(groupMember -> finalDateTime == null
                        //时间不为null，你需要在这个时间之后
                || groupMember.getUpdateAt().isAfter(finalDateTime))
                .map(GroupCard::new)
                .collect(Collectors.toList());
        return ResponseModel.buildOk(groupCards);

    }

    @GetMapping(value = "/{id}")
    @ApiOperation(value = "获取一个群的信息,必须是群成员")
    public ResponseModel<GroupCard> getGroup(@PathVariable String id,
                                             HttpServletRequest request) {
        if (Strings.isNullOrEmpty(id)) {
            return ResponseModel.buildParameterError();
        }
        User self = (User)request.getAttribute("aself");
        GroupMember member = groupService.getMember(self.getId(),id);
        if (member == null) {
            return ResponseModel.buildNotFoundGroupError(null);
        }

        Group group = groupService.getGroup(id);
        member.setGroup(group);

        return ResponseModel.buildOk(new GroupCard(member));

    }

    @GetMapping(value = "/{groupId}/members")
    @ApiOperation(value = "拉取一个群的所有成员，你必须是成员之一")
    public ResponseModel<List<GroupMemberCard>> members(@PathVariable String groupId,
                                                        HttpServletRequest request){
        User self = (User)request.getAttribute("aself");
        //没有这个群
        Group group = groupService.findById(groupId);
        if (group == null) {
            return ResponseModel.buildNotFoundGroupError(null);
        }

        // 检查权限
        GroupMember selfMember = groupService.getMember(self.getId(),groupId);
        if(selfMember == null){
            return ResponseModel.buildNoPermissionError();
        }
        //所有成员
        Set<GroupMember> members = groupService.getMembers(group);
        if(members == null){
            return ResponseModel.buildServiceError();
        }
        //返回
        List<GroupMemberCard> memberCards = members
                .stream()
                .map(GroupMemberCard::new)
                .collect(Collectors.toList());

        return ResponseModel.buildOk(memberCards);
    }


    @PostMapping(value = "/applyJoin/{groupId}")
    @ApiOperation(value = "拉申请加入一个群，\n" +
            "此时会创建一个加入的申请，并写入表；然后会给管理员发送消息\n" +
            "管理员同意，其实就是调用添加成员的接口把对应的用户添加进去")
    public ResponseModel<ApplyCard> join(@PathVariable String groupId) {


        return null;
    }


    @PostMapping(value = "/member/{memberId}")
    @ApiOperation(value = "更改成员信息，请求的人要么是管理员，要么就是成员本人")
    public ResponseModel<GroupMemberCard> modifyMember(@PathVariable String memberId,
                                                       @RequestBody GroupMemberUpdateModel model,HttpServletRequest request) {

        /*1.判断参数*/

        if (!GroupMemberUpdateModel.check(model)) {
            return ResponseModel.buildParameterError();
        }

        //拿到我的信息
        User self = (User)request.getAttribute("aself");

        //获取要修改群成员在那个群的信息需要修改
        Group group = groupService.findById(model.getGroupId());


        if (group == null) {
            return ResponseModel.buildParameterError();
        }
        /*2.校验参数*/

        //得到当前群下的所有群成员信息
        Set<GroupMember> members = groupService.getMembers(group);

        //拿到self在群成员的名片
        GroupMember selfMember = groupService.getMember(self.getId(),model.getGroupId());

        if (selfMember == null) {
            return ResponseModel.buildParameterError();
        }

        List<String> memberIds = members.stream().map(GroupMember::getId).collect(Collectors.toList());

        boolean isAlready = false;

        for (String id : memberIds){
            if (id.equalsIgnoreCase(memberId)) {
                isAlready  = true;
            }
        }
        if (!isAlready) {
            return ResponseModel.buildParameterError();
        }
        //1.自己只能修改自己的信息 只能修改自己在群中的别名
        //2.如果是管理员或是创建者 都能修改他人的权限和别名
        //权限错误 如果你是普通权限 同时 传入的memberId不是你自己的群成员id 那么就证明你想改别人的 ,返回权限不足

        if (selfMember.getPermissionType() == GroupMember.PERMISSION_TYPE_NONE && !selfMember.getId().equalsIgnoreCase(memberId)) {
            return ResponseModel.buildNoPermissionError();
        }

        /**修改信息*/
        boolean isAdmin = false;

        if(selfMember.getPermissionType() != GroupMember.NOTIFY_LEVEL_NONE) {
            isAdmin = true;
        }
        GroupMember member = groupService.updateMember(memberId,model,isAdmin);

        if (member == null) {
            return ResponseModel.buildServiceError();
        }
        //返回最新的GroupMemberCard
        return ResponseModel.buildOk(new GroupMemberCard(member));
    }

    @PostMapping(value = "/{groupId}/member")
    @ApiOperation(value = "给群添加成员的接口")
            public ResponseModel<List<GroupMemberCard>> memberAdd(@PathVariable String groupId,
                                                          @RequestBody GroupMemberAddModel model,
                                                                  HttpServletRequest request) {
        if (Strings.isNullOrEmpty(groupId) || !GroupMemberAddModel.check(model)) {
            return ResponseModel.buildParameterError();
        }
        //拿到我的信息
        User self = (User)request.getAttribute("aself");

        //移除之后再进行判断数量
        model.getUsers().remove(self.getId());
        if (model.getUsers().size() == 0) {
            return ResponseModel.buildParameterError();
        }
        //没有这个群
        Group group = groupService.findById(groupId);
        if(group == null){
            return ResponseModel.buildNotFoundGroupError(null);
        }
        //我必须是成员，同时是管理员及其以上级别
        GroupMember selfMember = groupService.getMember(self.getId(),groupId);
        if(selfMember == null || selfMember.getPermissionType() == GroupMember.PERMISSION_TYPE_NONE){
            return ResponseModel.buildNoPermissionError();
        }

        //已有的成员
        Set<GroupMember> oldMembers = groupService.getMembers(group);
        Set<String> oldMemberUserIds = oldMembers.stream()
                .map(GroupMember::getUserId)
                .collect(Collectors.toSet());

        List<User> insertUsers = new ArrayList<>();
        for (Object s:model.getUsers()){
            Gson gson = new Gson();
            String json = gson.toJson(s);
            JSONObject jsonObject = JSONObject.parseObject(json);
            String r = jsonObject.getString("id");
            //找人
            User user = userService.findById(r);
            if(user == null){
                continue;
                //已经在群里了
            }
            if(oldMemberUserIds.contains(user.getId())){
                continue;
            }
            insertUsers.add(user);
        }
        //没有一个新增的成员
        if(insertUsers.size() == 0){
            return ResponseModel.buildParameterError();
        }
        //进行添加操作
        Set<GroupMember> insertMembers = groupService.addMembers(group,insertUsers);
        if(insertMembers == null){
            return ResponseModel.buildServiceError();
        }

        //转换
        List<GroupMemberCard> insertCards = insertMembers.stream()
                .map(GroupMemberCard::new)
                .collect(Collectors.toList());

        //通知
        //1.通知新增的成员，你呗加入了XXX
     pushservice.pushGroupMemberAdd(oldMembers,insertCards);

        //2.通知群中老的成员,有XXX，XXX加入群
           pushservice.pushGroupMemberAdd(oldMembers,insertCards);

        return ResponseModel.buildOk(insertCards);

    }


}
