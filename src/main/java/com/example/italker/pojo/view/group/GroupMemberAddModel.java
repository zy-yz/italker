package com.example.italker.pojo.view.group;

import com.google.gson.annotations.Expose;

import java.util.HashSet;
import java.util.Set;

/**
 * @Description: 用户申请创建群的model
 * @Email: 1090712762@qq.com
 * @Author: Rattan Pepper
 * @Date: 2020/1/7
 */
public class GroupMemberAddModel {

    @Expose
    private Set<Object> users = new HashSet<>();

    public Set<Object> getUsers() {
        return users;
    }

    public void setUsers(Set<Object> users) {
        this.users = users;
    }

    public static boolean check(GroupMemberAddModel model) {
        return !(model.users == null
                || model.users.size() == 0);
    }
}
