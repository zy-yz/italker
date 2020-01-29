package com.example.italker.pojo.view.group;

import com.google.common.base.Strings;
import com.google.gson.annotations.Expose;

/**
 * @Description: 修改群成员信息的请求Model(例如管理员修改普通用户的别名)
 * @Email: 1090712762@qq.com
 * @Author: Rattan Pepper
 * @Date: 2020/1/7
 */
public class GroupMemberUpdateModel {
    @Expose
    private String alias;// 别名／备注
    @Expose
    private boolean isAdmin;// 是否是管理员
    @Expose
    private String groupId;// 对应的群Id

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public static boolean check(GroupMemberUpdateModel model) {
        return ((!Strings.isNullOrEmpty(model.alias) || model.isAdmin == true)
                && !Strings.isNullOrEmpty(model.groupId));
    }
}
