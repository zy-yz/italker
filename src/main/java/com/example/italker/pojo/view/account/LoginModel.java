package com.example.italker.pojo.view.account;

import com.google.common.base.Strings;
import com.google.gson.annotations.Expose;

/**
 * @Description: 客户端登录请求的实体
 * @Email: 1090712762@qq.com
 * @Author: Rattan Pepper
 * @Date: 2020/1/7
 */
public class LoginModel {
    @Expose
    private String account;
    @Expose
    private String password;
    @Expose
    private String pushId;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPushId() {
        return pushId;
    }

    public void setPushId(String pushId) {
        this.pushId = pushId;
    }

    // 校验
    public static boolean check(LoginModel model) {
        return model != null
                && !Strings.isNullOrEmpty(model.account)
                && !Strings.isNullOrEmpty(model.password);

    }
}

