package com.example.italker.service;


import com.alibaba.fastjson.JSON;
import com.example.italker.mapper.PushMapper;
import com.example.italker.mapper.UserMapper;
import com.example.italker.pojo.card.ApplyCard;
import com.example.italker.pojo.card.GroupMemberCard;
import com.example.italker.pojo.card.MessageCard;
import com.example.italker.pojo.card.UserCard;
import com.example.italker.pojo.entity.*;
import com.example.italker.pojo.view.base.PushModel;
import com.example.italker.utils.PushDispatcher;
import com.example.italker.utils.TextUtil;
import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Description: 消息存储与处理
 * @Email: 1090712762@qq.com
 * @Author: Rattan Pepper
 * @Date: 2020/1/14
 */
@Service
public class PushService {

    @Autowired
    private PushMapper pushMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private GroupService groupService;


    /**
     * 发送一条消息,并在当前的发送历史中记录存储记录*/
    public void pushNewMessage(User sender, Message message){
        if(sender == null || message == null){
            return;
        }

        //消息卡片用于发送
        MessageCard card = new MessageCard(message);
        //要推送的字符串
        //String entity = TextUtil.toJson(card);
        String entity = JSON.toJSONString(card);

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

            history.setSender(sender);
            history.setSenderId(sender.getId());

            history.setReceiverId(receiver.getId());

            //推送真实的Model
            PushModel pushModel = new PushModel();
            //每一条历史记录都是独立的,可以单独发送
            pushModel.add(history.getEntityType(),history.getEntity());

            //把需要发送的数据,丢给发送者进行发送
            dispatcher.add(receiver,pushModel);

            //保存到数据库
            pushMapper.saveHistory(history);
            /**
             * 这里他有更新发送者的信息，不知道到底用不用得着更新，现在没加
             * 应该要更新时间那些
             * */
        }else {
            Group group = message.getGroup();
            //因为延迟加载情况可能为null，需要通过ID查询
            if(group == null){
                group = groupService.findById(message.getGroupId());
            }
            //如果真的没有群,则返回
            if(group == null){
                return;
            }

            //给群成员发送信息
            Set<GroupMember> members = groupService.getMembers(group);
            if(members == null || members.size() == 0){
                return;
            }
            //过滤自己
            members = members.stream()
                    .filter(groupMember -> !groupMember.getUserId()
                    .equalsIgnoreCase(sender.getId()))
                    .collect(Collectors.toSet());
            if(members.size() == 0){
                return;
            }

            //一个历史记录表
            List<PushHistory> histories = new ArrayList<>();

            //推送的发送者，数据库要存储的列表,所有成员,要发送的数据，发送的类型
            addGroupMembersPushModel(dispatcher,
                    histories,
                    members,
                    entity,
                    PushModel.ENTITY_TYPE_MESSAGE);

            //保存到数据库的操作
            for(PushHistory history : histories){
                pushMapper.saveOrUpdate(history);
            }

        }
        //提交发送
        dispatcher.submit();
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
    private void addGroupMembersPushModel(PushDispatcher dispatcher,
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


    /**
     * @Description: 通知一些成员,被加入了XXX群
     * @Email: 1090712762@qq.com
     * @Author: Rattan Pepper
     * @Date: 2020/1/14
     */
    public void pushJoinGroup(Set<GroupMember> members){

        //发送者
        PushDispatcher dispatcher = new PushDispatcher();

        //一个历史记录列表
        List<PushHistory> histories = new ArrayList<>();

        for (GroupMember member : members){
            //User receiver = member.getUser();
            User receiver = userMapper.findUserById(member.getUserId());
            if(receiver == null){
                return;
            }

            //给每个成员的信息卡片
            GroupMemberCard memberCard = new GroupMemberCard(member);
            //String entity = TextUtil.toJson(memberCard);
            String entity = JSON.toJSONString(memberCard);

            //历史记录表字段建立
            PushHistory history = new PushHistory();
            // 你被添加到群的类型
            history.setEntityType(PushModel.ENTITY_TYPE_ADD_GROUP);
            history.setEntity(entity);
            history.setReceiver(receiver);
            history.setReceiverPushId(receiver.getPushId());
            histories.add(history);

            //构建一个消息model
            PushModel pushModel = new PushModel()
                    .add(history.getEntityType(),history.getEntity());

            //添加到发送者的数据集中
            dispatcher.add(receiver,pushModel);
            histories.add(history);
        }

        //保存到数据库中
        /**修改对象*/
//        for (PushHistory history : histories){
//            pushMapper.saveHistory(history);
//        }
        //提交发送
        dispatcher.submit();
    }


    /**
     * @Description: 通知老成员,有一系列新的成员加入到某个群
     * @Email: 1090712762@qq.com
     * @Author: Rattan Pepper
     * @Date: 2020/1/14
     */
    public void pushGroupMemberAdd(Set<GroupMember> oldMembers,
                                   List<GroupMemberCard> insertCards){

        //发送者
        PushDispatcher dispatcher = new PushDispatcher();

        //一个历史记录列表
        List<PushHistory> histories = new ArrayList<>();

        //当前新增的用户的集合的json字符串
        //String entity = TextUtil.toJson(insertCards);
        String entity = JSON.toJSONString(insertCards);

        // 进行循环添加，给oldMembers每一个老的用户构建一个消息，消息的内容为新增的用户的集合
        // 通知的类型是：群成员添加了的类型
        addGroupMembersPushModel(dispatcher, histories, oldMembers,
                entity, PushModel.ENTITY_TYPE_ADD_GROUP_MEMBERS);

        //保存到数据库中
        for (PushHistory history : histories){
            pushMapper.saveOrUpdate(history);
        }
        //提交发送
        dispatcher.submit();

    }

    /**
     * @Description: 给一个朋友推送我的信息过去,
     * @Email: 1090712762@qq.com
     * @Author: Rattan Pepper
     * @Date: 2020/1/14
     */
    public void pushFollow(User receiver, UserCard userCard){

        //一定是互相关注了
        userCard.setFollow(true);
        //String entity = TextUtil.toJson(userCard);
        String entity = JSON.toJSONString(userCard);


        // 历史记录表字段建立
        PushHistory history = new PushHistory();
        // 你被添加到群的类型
        history.setEntityType(PushModel.ENTITY_TYPE_ADD_FRIEND);
        history.setEntity(entity);
        history.setReceiverId(receiver.getId());
        history.setReceiverPushId(receiver.getPushId());
        // 保存到历史记录表
        pushMapper.saveHistory(history);

        // 推送
        PushDispatcher dispatcher = new PushDispatcher();
        PushModel pushModel = new PushModel()
                .add(history.getEntityType(), history.getEntity());
        dispatcher.add(receiver, pushModel);
        dispatcher.submit();

    }

    /**
     * @Description   给群主推送某人申请加入群的推送通知
     * @param [applyCard, ownerId]
     * @return void
     */
    public void pushGroupOwner(ApplyCard applyCard, String ownerId) {

        User receiver = userMapper.findUserById(ownerId);

        String entity = TextUtil.toJson(applyCard);

        String s;
        byte[] bytes = entity.getBytes();

        //个推推送工具类
        PushDispatcher dispatcher = new PushDispatcher();

        //构建推送历史消息model
        PushHistory pushHistory = new PushHistory();
        pushHistory.setEntityType(PushModel.ENTITY_TYPE_ADD_GROUP_MEMBERS);

        try {
            pushHistory.setEntity(new String(bytes,"UTF-8"));
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }

        pushHistory.setReceiverPushId(receiver.getPushId());
        pushHistory.setReceiver(receiver);

        pushMapper.saveHistory(pushHistory);

        //构建一个推送的Model
        PushModel pushModel = new PushModel();
        pushModel.add(pushHistory.getEntityType(),pushHistory.getEntity());

        //添加到发送者的数据集合中
        dispatcher.add(receiver, pushModel);

        //提交发送
        dispatcher.submit();
    }
}
