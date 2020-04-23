package com.example.italker.pojo.view.group;

import com.google.common.base.Strings;
import com.google.gson.annotations.Expose;

import java.util.HashSet;
import java.util.Set;
/**
 * @Description: 群添加的Model
 * @Email: 1090712762@qq.com
 * @Author: Rattan Pepper
 * @Date: 2020/1/7
 */
public class GroupCreateModel {
    @Expose
    private String name;
    @Expose
    private String desc;
    @Expose
    private String picture;
    @Expose
    private Set<Object> users = new HashSet<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public Set<Object> getUsers() {
        return users;
    }

    public void setUsers(Set<Object> users) {
        this.users = users;
    }

    public static boolean check(GroupCreateModel model) {
        return !(Strings.isNullOrEmpty(model.name)
                || Strings.isNullOrEmpty(model.desc)
                || Strings.isNullOrEmpty(model.picture)
                || model.users == null
                || model.users.size() == 0);
    }
}

