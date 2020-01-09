package com.example.italker.controller;

import com.example.italker.Base.Base;
import com.example.italker.pojo.card.UserCard;
import com.example.italker.pojo.entity.User;
import com.example.italker.pojo.view.base.ResponseModel;
import com.example.italker.pojo.view.user.UpdateInfoModel;
import com.example.italker.service.UserService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@RestController
@RequestMapping("/api/user")
public class UserController extends Base {

    @Autowired
    private UserService userService;

    @PostMapping(value = "/update")
    @ApiOperation(value = "用户信息修改接口,返回自己的个人信息")
    public ResponseModel<UserCard> update(UpdateInfoModel model) {
        if(!UpdateInfoModel.check(model)){
            return ResponseModel.buildParameterError();
        }
        User self = getSelf();
        //更新用户信息
        self = model.updateToUser(self);
        self = userService.update(self);
        //构架自己的用户信息
        UserCard card = new UserCard(self,true);
        //返回
        return ResponseModel.buildOk(card);
    }
}
