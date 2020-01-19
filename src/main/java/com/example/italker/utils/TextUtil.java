package com.example.italker.utils;


import com.example.italker.provider.GsonProvider;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Base64;

/**
 * @Description: 字符串转化工具类
 * @Email: 1090712762@qq.com
 * @Author: Rattan Pepper
 * @Date: 2020/1/6
 */
@Component
public class TextUtil {


    /**
     *
     *@description: 计算一个字符串的MD5信息
     *@time: 2020/1/6
     *@methodName:
     */
    public static String getMD5(String str){
        try {
            //生成一个MD5加密计算摘要
            MessageDigest md = MessageDigest.getInstance("MD5");
            //计算MD5函数
            md.update(str.getBytes());
            // digest()最后确定返回md5 hash值，返回值为8为字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
            // BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
            return new BigInteger(1,md.digest()).toString();
        }catch (Exception e){
            e.printStackTrace();
            return str;
        }
    }

    /**
     *
     *@description: 对一个字符串进行Base64编码
     *@time: 2020/1/6
     *@methodName:
     * @param str 原始字符串
     * @return 进行Base64编码后的字符串
     */
    public static String encodeBase64(String str){
        return Base64
                .getEncoder()
                .encodeToString(str.getBytes());
    }


//    public static String toJson(Object obj){
//        return GsonProvider.getGson().toJson(obj);
//    }
}
