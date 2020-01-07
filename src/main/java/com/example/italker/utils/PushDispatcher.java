package com.example.italker.utils;


import com.example.italker.pojo.entity.User;
import com.example.italker.pojo.view.base.PushModel;
import com.gexin.rp.sdk.base.IBatch;
import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.TransmissionTemplate;
import com.google.common.base.Strings;
import javafx.util.Builder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @Description: 消息推送工具类
 * @Email: 1090712762@qq.com
 * @Author: Rattan Pepper
 * @Date: 2020/1/7
 */
public class PushDispatcher {
    //采用"Java SDK 快速入门"， "第二步 获取访问凭证 "中获得的应用配置，用户可以自行替换
    private static final String appId = "Rr51sROK4B8FXbq0TUjAF5";
    private static final String appKey = "eurxTdqHECAKgc7s4xtUe9";
    private static final String masterSecret = "2zqRh5hMIY93LBqVlBtsi";
    private static final String host = "http://sdk.open.api.igexin.com/apiex.htm";

    private final IGtPush pusher;

    //要收到消息的人和内容的列表
    private final List<BatchBean> beans = new ArrayList<>();

    public PushDispatcher() {
        //最根本的发送者
        pusher = new IGtPush(host,appKey,masterSecret);
    }

    public boolean add(User receiver, PushModel model){
        //基础检查,必须有接受者的设备ID
        if(receiver == null || model == null || Strings.isNullOrEmpty(receiver.getPushId())){
            return false;
        }
        String pushString = model.getPushString();
        if(Strings.isNullOrEmpty(pushString)){
            return false;
        }

        //构建一个目标加内容
        BatchBean bean = buildMessage(receiver.getPushId(),pushString);
        beans.add(bean);
        return true;
    }

    private BatchBean buildMessage(String clientId,String text){
        //透传消息，不要通知栏显示，而是在messageReceiver收到
        TransmissionTemplate template = new TransmissionTemplate();
        template.setAppId(appId);
        template.setAppkey(appKey);
        template.setTransmissionContent(text);
        //这个type为int类型,填写1则自动启动APP
        template.setTransmissionType(0);

        SingleMessage message = new SingleMessage();
        //把透传消息设置到单消息模板中
        message.setData(template);
        //是否进行离线发送
        message.setOffline(true);
        //离线消息时常
        message.setOfflineExpireTime(24 * 3600 * 1000);

        //设置推送目标,填入appid和clientId
        Target target = new Target();
        target.setAppId(appId);
        target.setClientId(clientId);

        //返回一个封装
        return new BatchBean(message,target);
    }

    //进行消息最终发送
    public boolean submit(){
        //构建打包的工具类
        IBatch batch = pusher.getBatch();

        //是否有数据需要发送
        boolean haveData = false;

        for(BatchBean bean : beans){
            try {
                batch.add(bean.message,bean.target);
                haveData = true;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        //没有数据就直接返回
        if(!haveData){
            return false;
        }
        IPushResult result = null;
        try {
            result = batch.submit();
        }catch (IOException e){
            e.printStackTrace();
            //失败情况再尝试发送一次
            try {
                batch.retry();
            }catch (IOException e1){
                e1.printStackTrace();
            }
        }

        if(result != null){
            try {
                Logger.getLogger("PushDispatcher")
                        .log(Level.INFO,(String) result.getResponse().get("result"));
                return true;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        Logger.getLogger("PushDispatcher")
                .log(Level.WARNING,"推送服务器响应异常!");
        return false;
    }





    //给每一人发送消息的一个Bean封锁
    private static class BatchBean{
        SingleMessage message;
        Target target;

        BatchBean(SingleMessage message,Target target){
            this.message = message;
            this.target = target;
        }
    }
}
