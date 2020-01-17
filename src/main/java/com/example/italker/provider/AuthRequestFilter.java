package com.example.italker.provider;

import com.example.italker.pojo.entity.User;
import com.example.italker.pojo.view.base.ResponseModel;
import com.example.italker.service.UserService;
import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.servlet.annotation.WebFilter;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.security.Principal;


/**
 * @Description: 用于所有请求的接口的过滤器和拦截
 * @Email: 1090712762@qq.com
 * @Author: Rattan Pepper
 * @Date: 2020/1/11
 */

@Component
@Provider
public class AuthRequestFilter implements ContainerRequestFilter {

    @Autowired
    private UserService userService;



    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        //检查是否是登录注册接口
        UriInfo urlInfo=requestContext.getUriInfo();
        if(urlInfo.getPath().contains("account/login") ||
                urlInfo.getPath().contains("account/register")){
            //直接走正常逻辑，不做拦截
            return;
        }
        //从header中找到第一个token节点
        String token = requestContext.getHeaders().getFirst("token");
        if(!Strings.isNullOrEmpty(token)){
            //查询自己的信息
            final User self = userService.findByToken(token);
            if(self != null){
                //给当前请求添加一个上下文
                requestContext.setSecurityContext(new SecurityContext() {
                   //主体部分
                    @Override
                    public Principal getUserPrincipal() {
                        //User 实现Principal接口
                        return self;
                    }

                    @Override
                    public boolean isUserInRole(String s) {
                        //可以在这里写入用户权限,role是权限名
                        //可以管理管理员全向等
                        return true;
                    }

                    @Override
                    public boolean isSecure() {
                        //默认false,Https
                        return false;
                    }

                    @Override
                    public String getAuthenticationScheme() {
                        //不用理会
                        return null;
                    }
                });
                //写入上下文就返回
                return;
            }
        }
        //直接返回一个账户需要登录的Model
        ResponseModel model = ResponseModel.buildHaveAccountError();
        //构建一个返回
        Response response = Response.status(Response.Status.OK)
                .entity(model)
                .build();
        //拦截，停止一个请求的继续下发,调用该方法后之间返回请求
        //不会继续往下走
        requestContext.abortWith(response);
    }
}
