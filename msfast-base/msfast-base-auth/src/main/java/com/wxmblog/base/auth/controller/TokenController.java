package com.wxmblog.base.auth.controller;

import com.alibaba.fastjson.JSONObject;
import com.wxmblog.base.auth.authority.service.IAdminAuthorityService;
import com.wxmblog.base.auth.common.annotation.SuperAdminMethod;
import com.wxmblog.base.auth.common.enums.LoginType;
import com.wxmblog.base.auth.common.rest.request.*;
import com.wxmblog.base.auth.common.rest.response.LoginUserResponse;
import com.wxmblog.base.auth.common.validtype.*;
import com.wxmblog.base.auth.service.TokenService;
import com.wxmblog.base.auth.utils.ReflexUtils;
import com.wxmblog.base.auth.authority.service.IAuthorityService;
import com.wxmblog.base.common.annotation.AuthIgnore;
import com.wxmblog.base.common.constant.ParamTypeConstants;
import com.wxmblog.base.common.entity.LoginUser;
import com.wxmblog.base.common.utils.SpringUtils;
import com.wxmblog.base.common.utils.ViolationUtils;
import com.wxmblog.base.common.web.domain.R;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.List;

/**
 * @program: msfast-parent
 * @description:
 * @author: Mr.Wang
 * @create: 2022-06-16 10:37
 **/
@RestController
@RequestMapping("/token")
@Api(tags = "用户信息")
public class TokenController {

    @Autowired
    TokenService tokenService;

    @Autowired
    List<IAuthorityService> iAuthorityService;

    @AuthIgnore
    @PostMapping("/register")
    @ApiOperation(value = "手机号注册")
    @ApiOperationSort(1)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = ParamTypeConstants.requestBody, name = "body", value = "{\"phone\":\"手机号 必填\",\"verificationCode\":\"验证码 必填\",\"password\":\"登录密码 必填\",\"truePassword\":\"确认密码 必填\"}", required = true)
    })
    public R<Void> register(@RequestBody @ApiIgnore String viewmodelJson) {

        for (IAuthorityService iAuthorityService : iAuthorityService) {
            if (LoginType.Number_Password.equals(iAuthorityService.getLoginType())) {

                Class<? extends RegisterRequest> clsViewModel = ReflexUtils.getServiceView(iAuthorityService);
                RegisterRequest viewModel = JSONObject.parseObject(viewmodelJson, clsViewModel);
                //数据校验
                ViolationUtils.violation(viewModel, PhoneRegister.class);
                ViolationUtils.violation(viewModel);
                viewModel.setLoginType(LoginType.Number_Password);
                tokenService.register(viewModel);
                return R.ok();
            }

        }
        return R.fail("没有实现注册方法");
    }

    @AuthIgnore
    @PostMapping("/login")
    @ApiOperation(value = "手机号登陆")
    @ApiOperationSort(2)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = ParamTypeConstants.requestBody, name = "body", value = "{\"username\":\"用户名 必填\",\"password\":\"登录密码 必填\"}", required = true)
    })
    public R<LoginUserResponse> login(@RequestBody @ApiIgnore String viewmodelJson) {

        for (IAuthorityService iAuthorityService : iAuthorityService) {
            if (LoginType.Number_Password.equals(iAuthorityService.getLoginType())) {
                Class<? extends LoginRequest> clsViewModel = ReflexUtils.getServiceViewModel(iAuthorityService);

                LoginRequest viewModel = JSONObject.parseObject(viewmodelJson, clsViewModel);

                //数据校验
                ViolationUtils.violation(viewModel, PhoneLogin.class);
                ViolationUtils.violation(viewModel);
                viewModel.setLoginType(LoginType.Number_Password);
                return R.ok(tokenService.login(viewModel));
            }

        }

        return R.fail("没有实现登陆方法");
    }

    @AuthIgnore
    @PostMapping("/sms/login")
    @ApiOperation(value = "验证码登陆")
    @ApiOperationSort(3)
    public R<LoginUserResponse> login(@RequestBody @Valid SmsLoginRequest request) {

        return R.ok(tokenService.smsLogin(request));
    }


    @DeleteMapping("/logout")
    @ApiOperation(value = "退出登陆")
    @ApiOperationSort(4)
    public R<Void> logout() {
        tokenService.logout();
        return R.ok();
    }

    @AuthIgnore
    @ApiOperation(value = "发送短信验证码")
    @PostMapping("/sendsms")
    @ApiOperationSort(5)
    public R<Void> sendSms(@RequestBody @Valid SendSmsRequest sendSmsRequest) {
        tokenService.sendSms(sendSmsRequest);
        return R.ok();
    }

    @AuthIgnore
    @ApiOperation(value = "校验短信验证码")
    @PostMapping("/checksms")
    @ApiOperationSort(6)
    public R<Void> checkSms(@RequestBody @Valid CheckSmsRequest checkSmsRequest) {
        tokenService.checkSms(checkSmsRequest);
        return R.ok();
    }

    @AuthIgnore
    @PostMapping("/wxAppletRegister")
    @ApiOperation(value = "微信小程序注册")
    @ApiOperationSort(7)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = ParamTypeConstants.requestBody, name = "body", value = "{\"code\":\"code 必填\"}", required = true)
    })
    public R<Void> wxAppletRegister(@RequestBody @ApiIgnore String viewmodelJson) {

        for (IAuthorityService iAuthorityService : iAuthorityService) {
            if (LoginType.WX_Applet.equals(iAuthorityService.getLoginType())) {

                Class<? extends RegisterRequest> clsViewModel = ReflexUtils.getServiceView(iAuthorityService);
                RegisterRequest viewModel = JSONObject.parseObject(viewmodelJson, clsViewModel);
                //数据校验
                ViolationUtils.violation(viewModel, WxAppletRegister.class);
                ViolationUtils.violation(viewModel);
                viewModel.setLoginType(LoginType.WX_Applet);
                tokenService.wxAppletRegister(viewModel);
                return R.ok();
            }
        }
        return R.fail("没有实现注册方法");
    }

    @AuthIgnore
    @PostMapping("/wxAppletLogin")
    @ApiOperation(value = "微信小程序登陆")
    @ApiOperationSort(8)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = ParamTypeConstants.requestBody, name = "body", value = "{\"code\":\"code 必填\"}", required = true)
    })
    public R<LoginUserResponse> wxAppletLogin(@RequestBody @ApiIgnore String viewmodelJson) {

        for (IAuthorityService iAuthorityService : iAuthorityService) {
            if (LoginType.WX_Applet.equals(iAuthorityService.getLoginType())) {

                Class<? extends LoginRequest> clsViewModel = ReflexUtils.getServiceViewModel(iAuthorityService);
                LoginRequest viewModel = JSONObject.parseObject(viewmodelJson, clsViewModel);

                //数据校验
                ViolationUtils.violation(viewModel, WxAppletLogin.class);
                ViolationUtils.violation(viewModel);
                viewModel.setLoginType(LoginType.WX_Applet);
                return R.ok(tokenService.wxAppletLogin(viewModel));
            }
        }

        return R.fail("没有实现登陆方法");
    }

    @AuthIgnore
    @PostMapping("/admin/login")
    @ApiOperation(value = "后台登陆")
    @ApiOperationSort(9)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = ParamTypeConstants.requestBody, name = "body", value = "{\"username\":\"用户名 必填\",\"password\":\"登录密码 必填\"}", required = true)
    })
    public R<LoginUserResponse> adminLogin(@RequestBody @ApiIgnore String viewmodelJson) {


        for (IAuthorityService iAuthorityService : iAuthorityService) {
            if (LoginType.ADMIN.equals(iAuthorityService.getLoginType())) {

                Class<? extends LoginRequest> clsViewModel = ReflexUtils.getServiceViewModel(iAuthorityService);

                LoginRequest viewModel = JSONObject.parseObject(viewmodelJson, clsViewModel);

                //数据校验
                ViolationUtils.violation(viewModel, AdminLogin.class);
                ViolationUtils.violation(viewModel);
                viewModel.setLoginType(LoginType.ADMIN);
                return R.ok(tokenService.adminLogin(viewModel));
            }
        }

        return R.fail("没有实现登陆方法");
    }

    @ApiOperation("获取请求码")
    @ApiOperationSort(value = 10)
    @GetMapping("/getAuthKey")
    @AuthIgnore
    public R<String> getAuthKey() {
        return R.ok(tokenService.getAuthKey());
    }

    @ApiOperation("获取授权码")
    @ApiOperationSort(value = 11)
    @GetMapping("/getAuthCode")
    @SuperAdminMethod
    public R<String> getAuthCode(@RequestParam String authorizationKey) {
        return R.ok(tokenService.getAuthCode(authorizationKey));
    }

    @ApiOperation("校验授权")
    @ApiOperationSort(value = 12)
    @PostMapping("/authCheck")
    @AuthIgnore
    public R<Void> authCheck(@RequestBody AuthCheckRequest request) {
        tokenService.authCheck(request);
        return R.ok();
    }
}


