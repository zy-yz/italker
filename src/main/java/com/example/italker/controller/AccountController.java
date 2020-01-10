package com.example.italker.controller;

import com.example.italker.Base.Base;
import com.example.italker.pojo.entity.User;
import com.example.italker.pojo.view.account.AccountRspModel;
import com.example.italker.pojo.view.account.LoginModel;
import com.example.italker.pojo.view.account.RegisterModel;
import com.example.italker.pojo.view.base.ResponseModel;
import com.example.italker.service.AccountService;
import com.example.italker.service.UserService;
import com.google.common.base.Strings;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@RestController
@RequestMapping("/account")
public class AccountController extends Base {

    @Autowired
    private AccountService accountService;

    @Autowired
    private UserService userService;





    @PostMapping(value = "/login")
    @ApiOperation(value = "登录")
    public ResponseModel<AccountRspModel> login(@RequestBody LoginModel model){
        if(!LoginModel.check(model)){
            return ResponseModel.buildParameterError();
        }
        User user = userService.login(model.getAccount(),model.getPassword());
        if(user != null){
            //如果有携带PushId
            if(!Strings.isNullOrEmpty(model.getPushId())){
                return bind(user,model.getPushId());
            }
            AccountRspModel rspModel  = new AccountRspModel(user);
            return  ResponseModel.buildOk(rspModel);
        }else{
            return ResponseModel.buildLoginError();
        }
    }


    @PostMapping(value = "/register")
    @ApiOperation(value = "注册")
    public ResponseModel<AccountRspModel> register(@RequestBody RegisterModel model) {
        if (!RegisterModel.check(model)) {
            // 返回参数异常
            return ResponseModel.buildParameterError();
        }
        User user = userService.findByPhone(model.getAccount().trim());
        if (user != null) {
            // 已有账户
            return ResponseModel.buildHaveAccountError();
        }
        user = userService.findByName(model.getName().trim());
        if(user != null){
            //已有用户名
            return ResponseModel.buildHaveNameError();
        }

        //开始注册
        user = userService.register(model.getAccount(),
                model.getPassword(),
                model.getName());

        if(user != null){
            //如果有就携带PushId
            if(!Strings.isNullOrEmpty(model.getPushId())){
                return bind(user,model.getPushId());
            }
            //返回当前的账户
            AccountRspModel rspModel = new AccountRspModel(user);
            return ResponseModel.buildOk(rspModel);
        }else {
            //注册异常
            return ResponseModel.buildRegisterError();
        }

    }



    /**
     * 绑定的操作
     *
     * @param self   自己
     * @param pushId PushId
     * @return User
     */
    private ResponseModel<AccountRspModel> bind(User self,String pushId){
        //进行设备ID绑定的操作
        User user = userService.bindPushId(self,pushId);

        if(user == null){
            //绑定失败则是服务器异常
            return ResponseModel.buildServiceError();
        }
        //返回当前的账户,并且已经绑定了
        AccountRspModel rspModel = new AccountRspModel(user,true);
        return ResponseModel.buildOk(rspModel);
    }

}
