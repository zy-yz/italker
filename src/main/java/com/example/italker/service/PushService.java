package com.example.italker.service;


import com.example.italker.mapper.PushMapper;
import com.example.italker.pojo.entity.PushHistory;
import com.example.italker.pojo.entity.User;
import com.example.italker.pojo.view.base.PushModel;
import com.example.italker.utils.PushDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PushService {

    @Autowired
    private PushMapper pushMapper;





    /**
     * 推送账户退出消息
     *
     * @param receiver 接收者
     * @param pushId   这个时刻的接收者的设备Id
     */
    public void pushLogout(User receiver, String pushId) {
        //历史记录表字段
        PushHistory history = new PushHistory();
        //被添加到群的类型
        history.setEntityType(PushModel.ENTITY_TYPE_LOGOUT);
        history.setEntity("Account logout!!!");
        history.setReceiver(receiver);
        history.setReceiverPushId(pushId);

        //保存到历史记录表
        pushMapper.saveHistory(history);

        //发送者
        PushDispatcher dispatcher = new PushDispatcher();

        //具体推送的内容
        PushModel pushModel = new PushModel()
                .add(history.getEntityType(),history.getEntity());

        //添加并提交到第三方推送
        dispatcher.add(receiver,pushModel);
        dispatcher.submit();

    }
}
