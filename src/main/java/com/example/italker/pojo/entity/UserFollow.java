package com.example.italker.pojo.entity;

import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * @Description: 用户关系的Model，
 *  * 用于用户直接进行好友关系的实现
 * @Email: 1090712762@qq.com
 * @Author: Rattan Pepper
 * @Date: 2020/1/7
 */
@Table(name = "TB_USER_FOLLOW")
@Data
@Accessors(chain = true)
//public class UserFollow {
//
//    //这里要注意@ManyToOne(optional = false)
//    private String id;
//    private String originId;
//    private String targetId;
//    private String alias;
//
//
//
//    @JoinColumn(name = "originId")
//    private User origin;
//
//    // 定义关注的目标，你关注的人
//    // 也是多对1，你可以被很多人关注，每次一关注都是一条记录
//    // 所有就是 多个UserFollow 对应 一个 User 的情况
//    @ManyToOne(optional = false)
//    // 定义关联的表字段名为targetId，对应的是User.id
//    // 定义的是数据库中的存储字段
//    @JoinColumn(name = "targetId")
//    private User target;
//    private LocalDateTime createAt= LocalDateTime.now();
//    private LocalDateTime updateAt= LocalDateTime.now();
//
//    /*@Id
//    @PrimaryKeyJoinColumn
//    @GeneratedValue(generator = "uuid")
//    @GenericGenerator(name = "uuid", strategy = "uuid2")
//    @Column(updatable = false, nullable = false)
//    private String id;
//
//
//    // 定义一个发起人，你关注某人，这里就是你
//    // 多对1 -> 你可以关注很多人，你的每一次关注都是一条记录
//    // 你可以创建很多个关注的信息，所有是多对1；
//    // 这里的多对一是：User 对应 多个UserFollow
//    // optional 不可选，必须存储，一条关注记录一定要有一个"你"
//    @ManyToOne(optional = false)
//    // 定义关联的表字段名为originId，对应的是User.id
//    // 定义的是数据库中的存储字段
//    @JoinColumn(name = "originId")
//    private User origin;
//    // 把这个列提取到我们的Model中，不允许为null，不允许更新，插入
//    @Column(nullable = false, updatable = false, insertable = false)
//    private String originId;
//
//
//    // 定义关注的目标，你关注的人
//    // 也是多对1，你可以被很多人关注，每次一关注都是一条记录
//    // 所有就是 多个UserFollow 对应 一个 User 的情况
//    @ManyToOne(optional = false)
//    // 定义关联的表字段名为targetId，对应的是User.id
//    // 定义的是数据库中的存储字段
//    @JoinColumn(name = "targetId")
//    private User target;
//    // 把这个列提取到我们的Model中，不允许为null，不允许更新，插入
//    @Column(nullable = false, updatable = false, insertable = false)
//    private String targetId;
//
//
//    // 别名，也就是对target的备注名, 可以为null
//    @Column
//    private String alias;
//
//
//    // 定义为创建时间戳，在创建时就已经写入
//    @CreationTimestamp
//    @Column(nullable = false)
//    private LocalDateTime createAt = LocalDateTime.now();
//
//    // 定义为更新时间戳，在创建时就已经写入
//    @UpdateTimestamp
//    @Column(nullable = false)
//    private LocalDateTime updateAt = LocalDateTime.now();
//
//
//    public String getId() {
//        return id;
//    }
//
//    public void setId(String id) {
//        this.id = id;
//    }
//
//    public User getOrigin() {
//        return origin;
//    }
//
//    public void setOrigin(User origin) {
//        this.origin = origin;
//    }
//
//    public String getOriginId() {
//        return originId;
//    }
//
//    public void setOriginId(String originId) {
//        this.originId = originId;
//    }
//
//    public User getTarget() {
//        return target;
//    }
//
//    public void setTarget(User target) {
//        this.target = target;
//    }
//
//    public String getTargetId() {
//        return targetId;
//    }
//
//    public void setTargetId(String targetId) {
//        this.targetId = targetId;
//    }
//
//    public String getAlias() {
//        return alias;
//    }
//
//    public void setAlias(String alias) {
//        this.alias = alias;
//    }
//
//    public LocalDateTime getCreateAt() {
//        return createAt;
//    }
//
//    public void setCreateAt(LocalDateTime createAt) {
//        this.createAt = createAt;
//    }
//
//    public LocalDateTime getUpdateAt() {
//        return updateAt;
//    }
//
//    public void setUpdateAt(LocalDateTime updateAt) {
//        this.updateAt = updateAt;
//    }*/
//}
//
//
//
public class UserFollow {

//    @Id
//    @PrimaryKeyJoinColumn
//    @GeneratedValue(generator = "uuid")
//    @GenericGenerator(name = "uuid", strategy = "uuid2")
//    @Column(updatable = false, nullable = false)
    private String id;

    /**
     * 定义一个发起人，你关注某人，这里就是你
     * 多对一 你可以关注很多人，你的每一次关注都是一条记录
     * 你可以创建很多个关注的信息，所有都是多对一
     * 这里的多对一: 一个User 对应 多个UserFollow
     * optional 不可选 必须存储，一条关注记录一定要有一个“你”
     */
    @ManyToOne(optional = false)
    //定义关联的表字段名为 originId，对应是 User.id
    @JoinColumn(name = "originId")
    private User origin;

    //把这个列提取到model中，不允许为空，不允许更新、插入
    @Column(updatable = false, nullable = false, insertable = false)
    private String originId;

    /**
     * 定义关注的目标，你关注某人，这里就某人
     * 多对一 你可以被很多人关注，每一次关注都是一条记录
     * 即 多个UserFollow 对应 一个User 的情况
     */
    @ManyToOne(optional = false)
    //定义关联的表字段名为 targetId，对应是 User.id
    @JoinColumn(name = "targetId")
    private User target;

    //把这个列提取到model中，不允许为空，不允许更新、插入
    @Column(updatable = false, nullable = false, insertable = false)
    private String targetId;

    /**
     * 别名，即 你对target的备注名，可以为空
     */
    @Column
    private String alias;

    //定义为创建时间戳，在创建时就已经写入
    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createAt = LocalDateTime.now();

    //定义为更新时间戳
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updateAt = LocalDateTime.now();


    //我关注的人的列表方法
    //对应的数据库表字段为 TB_USER_FOLLOW.originId
    @JoinColumn(name = "originId")
    //定义为懒加载，定义加载User信息时并不查询这个集合
    @LazyCollection(LazyCollectionOption.EXTRA)
    //一对多，一个用户可以有很多关注人，每一次关注都是一个记录
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<UserFollow> following = new HashSet<>();

    //关注我的人列表
    //对应的数据库表字段为 TB_USER_FOLLOW.targetId
    @JoinColumn(name = "targetId")
    //定义为懒加载，定义加载User信息时并不查询这个集合
    @LazyCollection(LazyCollectionOption.EXTRA)
    //一对多，一个用户可以被很多人关注，每一次关注都是一个记录
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<UserFollow> followers = new HashSet<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getOrigin() {
        return origin;
    }

    public void setOrigin(User origin) {
        this.origin = origin;
    }

    public String getOriginId() {
        return originId;
    }

    public void setOriginId(String originId) {
        this.originId = originId;
    }

    public User getTarget() {
        return target;
    }

    public void setTarget(User target) {
        this.target = target;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }

    public Set<UserFollow> getFollowing() {
        return following;
    }

    public void setFollowing(Set<UserFollow> following) {
        this.following = following;
    }

    public Set<UserFollow> getFollowers() {
        return followers;
    }

    public void setFollowers(Set<UserFollow> followers) {
        this.followers = followers;
    }
}
