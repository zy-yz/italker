package com.example.italker.pojo.view.base;

import com.alibaba.fastjson.JSON;
import com.example.italker.utils.TextUtil;
import com.google.gson.annotations.Expose;

import javax.swing.text.html.parser.Entity;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 *  一个推送的具体Model，内部维持了一个数组，可以添加多个实体
 *  每次推送的详细数据是：把实体数组进行Json操作，然后发送Json字符串
 *  这样做的目的是：减少多次推送，如果有多个消息需要推送可以合并进行
 * @Email: 1090712762@qq.com
 * @Author: Rattan Pepper
 * @Date: 2020/1/7
 */
@SuppressWarnings("WeakerAccess")
public class PushModel {
    public static final int ENTITY_TYPE_LOGOUT = -1;
    public static final int ENTITY_TYPE_MESSAGE = 200;
    public static final int ENTITY_TYPE_ADD_FRIEND = 1001;
    public static final int ENTITY_TYPE_ADD_GROUP = 1002;
    public static final int ENTITY_TYPE_ADD_GROUP_MEMBERS = 1003;
    public static final int ENTITY_TYPE_MODIFY_GROUP_MEMBERS = 2001;
    public static final int ENTITY_TYPE_EXIT_GROUP_MEMBERS = 3001;

    private List<Entity> entities = new ArrayList<>();

    public PushModel add(Entity entity) {
        entities.add(entity);
        return this;
    }

    public PushModel add(int type, String content) {
        return add(new Entity(type, content));
    }

    //拿到一个推送的字符串
    public String getPushString(){
        if(entities.size() == 0){
            return null;
        }
        //return TextUtil.toJson(entities);
        return JSON.toJSONString(entities);
    }



    /**
     *
     *@description:具体的实体类型，在这个实体中包装了实体的内容和类型
     *@time: 2020/1/7
     *@methodName:
     */
    public static class Entity{
        public Entity(int type,String content){
            this.type = type;
            this.content = content;
        }
        //不需要完全序列化model字段时，我们就可以使用 @Expose 来结局。
        // 消息类型
        @Expose
        public int type;
        // 消息实体
        @Expose
        public String content;
        // 消息生成时间
        @Expose
        public LocalDateTime createAt = LocalDateTime.now();
    }
}
