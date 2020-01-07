package com.example.italker.utils;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import javax.annotation.Resource;

public class SessionFac {

    //全局sessionFactory
    @Resource
    private static SessionFactory sessionFactory;

    /**获取全局的SessionFactory
     * */
    public static SessionFactory sessionFactory(){
        return sessionFactory;
    }

    /**从sessionFactory中得到一个session对话
     *
     * @return
     */
    public static Session session(){
        return sessionFactory.getCurrentSession();
    }

    /**
     * 关闭sessionFactory*/
    public static void closeFactory(){
        if(sessionFactory !=null){
            sessionFactory.close();
        }
    }

    /**
     * 用户的实际操作的一个借口*/
    public interface QueryOnly{
        void query(Session session);
    }

    /**
     * 简化session事务操作的一个工具方法*/
    public static void queryOnly(QueryOnly query){
        //重开一个session
        Session session = sessionFactory().openSession();
        //开启事务
        final Transaction transtion  = session.beginTransaction();

        try {
            //调用传递进来的接口
            //并调用接口的方法把session传递进去
            query.query(session);
            //提交
            transtion.commit();
        }catch (Exception e){
            e.printStackTrace();
            //回滚
            try {
                transtion.rollback();
            }catch (RuntimeException e1){
                e1.printStackTrace();
            }
        }finally {
            //无论成功还是失败,都要关闭session
            session.close();
        }
    }

    // 用户的实际的操作的一个接口
    // 具有返回值T
    public interface Query<T> {
        T query(Session session);
    }

    //用户实际操作的一个接口
    //具有一个返回值
    public static <T> T query(Query<T> query) {
        // 重开一个Session
        Session session = sessionFactory.openSession();
        // 开启事物
        final Transaction transaction = session.beginTransaction();

        T t = null;
        try {
            // 调用传递进来的接口，
            // 并调用接口的方法把Session传递进去
            t = query.query(session);
            // 提交
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            // 回滚
            try {
                transaction.rollback();
            } catch (RuntimeException e1) {
                e1.printStackTrace();
            }
        } finally {
            // 无论成功失败，都需要关闭Session
            session.close();
        }

        return t;
    }

}
