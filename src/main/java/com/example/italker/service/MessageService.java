package com.example.italker.service;

import com.example.italker.mapper.MessageMapper;
import com.example.italker.pojo.entity.Group;
import com.example.italker.pojo.entity.Message;
import com.example.italker.pojo.entity.User;
import com.example.italker.pojo.view.message.MessageCreateModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

    @Autowired
    private MessageMapper messageMapper;

    /**
     * 查询某一个信息*/
    public Message findByIdMessage(String id) {

        return messageMapper.findByIdMessage(id);
    }

    /**添加一条普通消息*/
    public Message add(User sender, User receiver, MessageCreateModel model) {
        Message message = new Message(sender,receiver,model);
        return save(message);
    }


    /**添加一条群消息*/
    public Message add(User sender, Group group, MessageCreateModel model) {
        Message message = new Message(sender, group, model);
        return save(message);
    }


    /**SQL 填写*/
    private Message save(Message message){
        messageMapper.save(message);
        return messageMapper.getMessage(message);
    }
}
