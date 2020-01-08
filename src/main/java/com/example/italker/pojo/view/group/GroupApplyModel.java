package com.example.italker.pojo.view.group;

import com.google.gson.annotations.Expose;

/**
 * @Description: 申请加入群的model
 * @Email: 1090712762@qq.com
 * @Author: Rattan Pepper
 * @Date: 2020/1/7
 */
public class GroupApplyModel {

    @Expose
    private String desciption;
    @Expose
    private String attach;

    public String getDesciption() {
        return desciption;
    }

    public void setDesciption(String desciption) {
        this.desciption = desciption;
    }

    public String getAttach() {
        return attach;
    }

    public void setAttach(String attach) {
        this.attach = attach;
    }
}
