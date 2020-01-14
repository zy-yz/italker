package com.example.italker.mapper;

import com.example.italker.pojo.entity.Message;
import org.springframework.stereotype.Component;

@Component
public interface MessageMapper {


    Message findById(String id);

    void save(Message message);

    Message getMessage(Message message);
}
