package com.example.italker.service;


import com.example.italker.mapper.PushMapper;
import com.example.italker.pojo.card.MessageCard;
import com.example.italker.pojo.entity.GroupMember;
import com.example.italker.pojo.entity.Message;
import com.example.italker.pojo.entity.PushHistory;
import com.example.italker.pojo.entity.User;
import com.example.italker.pojo.view.base.PushModel;
import com.example.italker.utils.PushDispatcher;
import com.example.italker.utils.TextUtil;
import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class PushService {

    @Autowired
    private PushMapper pushMapper;

    @Autowired
    private UserService userService;


    /**
     * 发送一条消息,并在当前的发送历史中记录存储记录*/
    public void pushNewMessage(User sender, Message message){
        if(sender == null || message == null){
            return;
        }

        //消息卡片用于发送
        MessageCard card = new MessageCard(message);
        //要推送的字符串
        String entity = TextUtil.toJson(card);

        //发送者
        PushDispatcher dispatcher = new PushDispatcher();

        if(message.getGroup() == null
        && Strings.isNullOrEmpty(message.getGroupId())){
            //给朋友发送消息
            User receiver = userService.findById(message.getReceiverId());

            if(receiver == null){
                return;
            }

            //历史记录表字段建立
            PushHistory history = new PushHistory();
            //普通消息类型
            history.setEntityType(PushModel.ENTITY_TYPE_MESSAGE);
            history.setEntity(entity);
            history.setReceiver(receiver);
            //接受者当前的设备推送Id
            history.setReceiverPushId(receiver.getPushId());

            //推送真实的Model
            PushModel pushModel = new PushModel();
            //每一条历史记录都是独立的,可以单独发送
            pushModel.add(history.getEntityType(),history.getEntity());

            //把需要发送的数据,丢给发送者进行发送
            dispatcher.add(receiver,pushModel);

            //保存到数据库
            pushMapper.saveHistory(history);
            /**
             * 这里他有更新发送者的信息，不知道到底用不用得着更新，现在没加*/


        }
    }










    /**
     * 推送账户退出消息
     *
     * @param receiver 接收者
     * @param pushId   这个时刻的接收者的设备Id
     */
    public void pushLogout(User receiver, String pushId) {
        //历史记录表字段
        PushHistory history = new PushHistory();
        //被添加到群的类型
        history.setEntityType(PushModel.ENTITY_TYPE_LOGOUT);
        history.setEntity("Account logout!!!");
        history.setReceiver(receiver);
        history.setReceiverPushId(pushId);

        //保存到历史记录表
        pushMapper.saveHistory(history);

        //发送者
        PushDispatcher dispatcher = new PushDispatcher();

        //具体推送的内容
        PushModel pushModel = new PushModel()
                .add(history.getEntityType(),history.getEntity());

        //添加并提交到第三方推送
        dispatcher.add(receiver,pushModel);
        dispatcher.submit();

    }



    /**
     * 给群成员构建一个消息
     * 把消息存储到数据库的历史记录表中,每个人,每条消息都是一个记录*/
    private void addGroupMemberPushModel(PushDispatcher dispatcher,
                                         List<PushHistory> histories,
                                         Set<GroupMember> members,
                                         String entity,
                                         int entityTypeMessage){
        for (GroupMember member : members){
            //无须通过ID找用户
            User receiver = member.getUser();
            if(receiver == null){
                return;
            }
            //历史记录表字段
            PushHistory history = new PushHistory();
            history.setEntityType(entityTypeMessage);
            history.setEntity(entity);
            history.setReceiver(receiver);
            history.setReceiverPushId(receiver.getPushId());
            histories.add(history);

            //构建一个消息Model
            PushModel pushModel = new PushModel();
            pushModel.add(history.getEntityType(),history.getEntity());

            //添加到发送者的数据集中
            dispatcher.add(receiver,pushModel);
        }
    }
}
