package com.example.italker.mapper;


import com.example.italker.pojo.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface UserMapper {

     //User updateUser(User user);

     User findByPhone(String phone);

     User findByName(String name);

     Integer insertUser(User user);

     List<User> findByPushId(String id, String pushId);

     //void saveOrUpdateUserPushId(User user);

     User findUserById(String id);

     void saveOrUpdate(User user);
}
