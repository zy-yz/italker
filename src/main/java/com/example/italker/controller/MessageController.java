package com.example.italker.controller;


import com.example.italker.pojo.card.MessageCard;
import com.example.italker.pojo.entity.Group;
import com.example.italker.pojo.entity.Message;
import com.example.italker.pojo.entity.User;
import com.example.italker.pojo.view.base.ResponseModel;
import com.example.italker.pojo.view.message.MessageCreateModel;
import com.example.italker.service.GroupService;
import com.example.italker.service.MessageService;
import com.example.italker.service.PushService;
import com.example.italker.service.UserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.soap.MessageFactory;


/**
 * @Description: 消息发送的入口
 * @Email: 1090712762@qq.com
 * @Author: Rattan Pepper
 * @Date: 2020/1/14
 */
@RestController
@RequestMapping("/account")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @Autowired
    private PushService pushService;

    @Autowired
    private GroupService groupService;

    /**发送一条消息到服务器*/
    @PostMapping(value = "")
    @ApiOperation(value = "登录")
    public ResponseModel<MessageCard> pushMessage(MessageCreateModel model) {
        if (!MessageCreateModel.check(model)) {
            return ResponseModel.buildParameterError();
        }

        User self = new User();

        // 查询是否已经在数据库中有了
        Message message =messageService.findById(model.getId());
        if(message != null){
            //正常返回
            return ResponseModel.buildOk(new MessageCard(message));
        }

        if(model.getReceiverType() == Message.RECEIVER_TYPE_GROUP){
            return pushToGroup(self,model);
        }else {
            return pushToUser(self,model);
        }
    }

    //发送到人
    private ResponseModel<MessageCard> pushToUser(User sender,MessageCreateModel model){
        User receiver = userService.findById(model.getReceiverId());
        if(receiver == null){
            //没有找到接受者
            return ResponseModel.buildNotFoundUserError("Can`t find receiver user");

        }
        if(receiver.getId().equalsIgnoreCase(sender.getId())){
            //发送者接受者是同一个人就返回创建消息失败
            return ResponseModel.buildCreateError(ResponseModel.ERROR_CREATE_MESSAGE);
        }
        //存储数据库
        Message message = messageService.add(sender,receiver,model);

        return buildAndPushResponse(sender,message);
    }

    // 发送到群
    private ResponseModel<MessageCard> pushToGroup(User sender, MessageCreateModel model) {
        // 找群是有权限性质的找
        Group group = groupService.findById(sender, model.getReceiverId());
        if (group == null) {
            // 没有找到接收者群，有可能是你不是群的成员
            return ResponseModel.buildNotFoundUserError("Con't find receiver group");
        }

        // 添加到数据库
        Message message = messageService.add(sender, group, model);

        // 走通用的推送逻辑
        return buildAndPushResponse(sender, message);
    }

    //推送并构建一个返回消息
    private ResponseModel<MessageCard> buildAndPushResponse(User sender,Message message){
        if(message == null){
            //存储数据库失败
            return ResponseModel.buildCreateError(ResponseModel.ERROR_CREATE_MESSAGE);
        }
        //进行推送
        pushService.pushNewMessage(sender,message);

        //返回
        return ResponseModel.buildOk(new MessageCard(message));
    }

}
