package com.example.italker.mapper;

import com.example.italker.pojo.entity.PushHistory;
import com.example.italker.pojo.entity.User;
import com.example.italker.pojo.entity.UserFollow;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public interface PushMapper {


     void saveHistory(PushHistory history);

    void saveOrUpdate(PushHistory history);

    Set<UserFollow> getFollowById(String id);
}
