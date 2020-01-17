package com.example.italker.mapper;


import com.example.italker.pojo.entity.User;
import com.example.italker.pojo.entity.UserFollow;
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

     /**登录查找用户,使用密码和手机*/
    User loginFind(String account, String encodePassword);

    User findUserByToken(String token);

    UserFollow getUserFollow(String originId, String targetId);

    List<User> search(String name);

    void insertUserFollow(UserFollow follow);
}
