package com.example.italker.controller;
import com.example.italker.pojo.card.UserCard;
import com.example.italker.pojo.entity.User;
import com.example.italker.pojo.view.base.ResponseModel;
import com.example.italker.pojo.view.user.UpdateInfoModel;
import com.example.italker.service.PushService;
import com.example.italker.service.UserService;
import com.google.common.base.Strings;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.PathParam;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 用户信息的处理
 * @Email: 1090712762@qq.com
 * @Author: Rattan Pepper
 * @Date: 2020/1/16
 */
@RestController
@RequestMapping("/user")
public class UserController{

    @Autowired
    private UserService userService;

    @Autowired
    private PushService pushService;

    @PostMapping(value = "/update")
    @ApiOperation(value = "用户信息修改接口,返回自己的个人信息")
    public ResponseModel<UserCard> update(@RequestBody UpdateInfoModel model,HttpServletRequest request) {
        if(!UpdateInfoModel.check(model)){
            return ResponseModel.buildParameterError();
        }
        User self = new User();
        self = (User)request.getAttribute("aself");

        //更新用户信息
        self = model.updateToUser(self);
        self = userService.update(self);
        //构架自己的用户信息
        UserCard card = new UserCard(self,true);
        //返回
        return ResponseModel.buildOk(card);
    }


    @GetMapping(value="/contact")
    @ApiOperation(value="拉取联系人")
    public ResponseModel<List<UserCard>> contact(HttpServletRequest request) {
        User self = (User)request.getAttribute("aself");

        //拿到我的联系人
        List<User> users = userService.contacts(self);
        //转换为UserCard
        List<UserCard> userCards = users.stream()
                .map(user ->new UserCard(user,true))
                .collect(Collectors.toList());
        //返回;
        return ResponseModel.buildOk(userCards);
    }


    @GetMapping(value="{id}")
    @ApiOperation(value="拉取某人的信息")
    public ResponseModel<UserCard> getUser(HttpServletRequest request,@PathVariable String id) {
        if (Strings.isNullOrEmpty(id)) {
            // 返回参数异常
            return ResponseModel.buildParameterError();
        }

        User self = (User)request.getAttribute("aself");
        if (self.getId().equalsIgnoreCase(id)) {
            // 返回自己，不必查询数据库
            return ResponseModel.buildOk(new UserCard(self, true));
        }

        User user = userService.findById(id);
        if (user == null) {
            // 没找到，返回没找到用户
            return ResponseModel.buildNotFoundUserError(null);
        }
        // 如果我们直接有关注的记录，则我已关注需要查询信息的用户
        boolean isFollow = userService.getUserFollow(self,user) != null;
        return ResponseModel.buildOk(new UserCard(user,isFollow));
    }

    @GetMapping(value = "/search/{name}") // 名字为任意字符，可以为空
    @ApiOperation(value="搜索人的接口实现")
    public ResponseModel<List<UserCard>> search(@PathVariable String name,HttpServletRequest request) {

        User self = (User)request.getAttribute("aself");

        //先查询数据
        List<User> searchUsers = userService.search(name);
        // 把查询的人封装为UserCard
        // 判断这些人是否有我已经关注的人，
        // 如果有，则返回的关注状态中应该已经设置好状态

        //拿出我的联系人
        List<User> contacts = userService.contacts(self);

        //把User -> UserCard
        List<UserCard> userCards = searchUsers.stream()
                .map(user -> {
                    //判断这个人是不是我，或者是否在我的联系人中
                    boolean isFollow = user.getId().equalsIgnoreCase(self.getId()) ||
                            //进行联系人的任意匹配，匹配其中的Id字段
                            contacts
                                    .stream()
                                    .anyMatch(contactUser ->
                                            contactUser.getId()
                                                    .equalsIgnoreCase(user.getId()));
                    return new UserCard(user, isFollow);
                }).collect(Collectors.toList());

        //返回
        return ResponseModel.buildOk(userCards);
    }


    @PostMapping(value="/follow/{followId}")
    @ApiOperation(value="关注人，简化：关注人的操作其实是双方同时关注")
    public ResponseModel<UserCard> follow(@PathVariable String followId,HttpServletRequest request) {

        User self = new User();
        self = (User)request.getAttribute("aself");

        // 不能关注我自己
        if (self.getId().equalsIgnoreCase(followId)
                || Strings.isNullOrEmpty(followId)) {
            // 返回参数异常
            return ResponseModel.buildParameterError();
        }

        //找到我也关注的人
        User followUser = userService.findById(followId);
        if(followUser == null){
            //未找到人
            return ResponseModel.buildNotFoundUserError(null);
        }

        //备注默认没有，后面扩展
        followUser = userService.follow(self,followUser,null);

        if (followUser == null) {
            // 关注失败，返回服务器异常
            return ResponseModel.buildServiceError();
        }

        //通知我关注的人我关注他
        //给他发送一个我的信息过去
        pushService.pushFollow(followUser,new UserCard(self));

        //返回关注的人的信息
        return ResponseModel.buildOk(new UserCard(followUser,true));

    }
    }
