/**
 * 严肃声明：
 * 开源版本请务必保留此注释头信息，若删除我方将保留所有法律责任追究！
 * 本系统已申请软件著作权，受国家版权局知识产权以及国家计算机软件著作权保护！
 * 可正常分享和学习源码，不得用于违法犯罪活动，违者必究！
 * Copyright (c) 2019-2021 十三 all rights reserved.
 * 版权所有，侵权必究！
 */
package ltd.newbee.mall.api.admin;

import io.swagger.annotations.Api;
import ltd.newbee.mall.api.admin.param.*;
import ltd.newbee.mall.common.Constants;
import ltd.newbee.mall.common.ServiceResultEnum;
import ltd.newbee.mall.config.annotation.TokenToAdminUser;
import ltd.newbee.mall.entity.AdminUser;
import ltd.newbee.mall.entity.AdminUserToken;
import ltd.newbee.mall.service.AdminUserService;
import ltd.newbee.mall.util.Result;
import ltd.newbee.mall.util.ResultGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @author 13
 * @qq交流群 796794009
 * @email 2449207463@qq.com
 * @link https://github.com/newbee-ltd
 */
@RestController
@Api(value = "v1", tags = "8-0.后台管理系统管理员模块接口")
@RequestMapping("/manage-api/v1")
public class NewBeeAdminManageUserAPI {

    @Resource
    private AdminUserService adminUserService;

    private static final Logger logger = LoggerFactory.getLogger(NewBeeAdminManageUserAPI.class);

    private static final String ADMIN_USER = "adminUser:{}";
    /**
     * 登录
     * @param adminLoginParam
     * @return
     */
    @PostMapping(value = "/adminUser/login")
    public Result<String> login(@RequestBody @Valid AdminLoginParam adminLoginParam) {
        String loginResult = adminUserService.login(adminLoginParam.getUserName(), adminLoginParam.getPasswordMd5());
        logger.info("manage login api,adminName={},loginResult={}", adminLoginParam.getUserName(), loginResult);

        //登录成功
        if (StringUtils.hasLength(loginResult) && loginResult.length() == Constants.TOKEN_LENGTH) {
            Result result = ResultGenerator.genSuccessResult();
            result.setData(loginResult);
            return result;
        }
        //登录失败
        return ResultGenerator.genFailResult(loginResult);
    }

    /**
     * 管理员获显示自己信息，密码不会直接显示
     * 当管理员未登录时无效
     * @param adminUser
     * @return
     */
    @GetMapping(value = "/adminUser/profile")
    public Result profile(@TokenToAdminUser AdminUserToken adminUser) {
        logger.info("{}", ADMIN_USER);
        AdminUser adminUserEntity = adminUserService.getUserDetailById(adminUser.getAdminUserId());
        if (adminUserEntity != null) {
            adminUserEntity.setLoginPassword("******");
            Result result = ResultGenerator.genSuccessResult();
            result.setData(adminUserEntity);
            return result;
        }
        return ResultGenerator.genFailResult(ServiceResultEnum.DATA_NOT_EXIST.getResult());
    }

    /**
     * 修改管理员密码，旧密码也要提供
     * @param adminPasswordParam
     * @param adminUser
     * @return
     */
    @PutMapping(value = "/adminUser/password")
    public Result passwordUpdate(@RequestBody @Valid UpdateAdminPasswordParam adminPasswordParam, @TokenToAdminUser AdminUserToken adminUser) {
        logger.info("{}", ADMIN_USER);
        if (adminUserService.updatePassword(adminUser.getAdminUserId(), adminPasswordParam.getOriginalPassword(), adminPasswordParam.getNewPassword())) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(ServiceResultEnum.DB_ERROR.getResult());
        }
    }

    /**
     * 修改管理员用户名和昵称
     * @param adminNameParam
     * @param adminUser
     * @return
     */
    @PutMapping(value = "/adminUser/name")
    public Result nameUpdate(@RequestBody @Valid UpdateAdminNameParam adminNameParam, @TokenToAdminUser AdminUserToken adminUser) {
        logger.info("{}", ADMIN_USER);
        if (adminUserService.updateName(adminUser.getAdminUserId(), adminNameParam.getLoginUserName(), adminNameParam.getNickName())) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(ServiceResultEnum.DB_ERROR.getResult());
        }
    }

    /**
     * 管理员通过Id删除用户
     * @param deleteUserParam
     * @param adminUser
     * @return
     */
    @DeleteMapping("/adminUser/deleteUser")
    public Result deleteUser(@Valid @RequestBody DeleteUserParam deleteUserParam, @TokenToAdminUser AdminUserToken adminUser) {
        logger.info("{}", ADMIN_USER);
        if(adminUserService.deleteById(deleteUserParam.getUserId())) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(ServiceResultEnum.ID_NOT_EXIST.getResult());
        }
    }
    /**
     * 管理员通过Id删除用户
     * @param deleteAdminUserParam
     * @param adminUser
     * @return
     */
    @DeleteMapping(value ="/adminUser/deleteAdminUser")
    public Result deleteAdminUser(@Valid @RequestBody DeleteAdminUserParam deleteAdminUserParam, @TokenToAdminUser AdminUserToken adminUser) {
        logger.info("{}", ADMIN_USER);
        if(adminUserService.deleteAdminUserById(deleteAdminUserParam.getAdminUserId())){
            return  ResultGenerator.genSuccessResult();
        }else{
            return ResultGenerator.genFailResult(ServiceResultEnum.ID_NOT_EXIST.getResult());
        }
    }


    /**
     * 登出
     * @param adminUser
     * @return
     */
    @DeleteMapping(value = "/logout")
    public Result logout(@TokenToAdminUser AdminUserToken adminUser) {
        logger.info("{}", ADMIN_USER);
        adminUserService.logout(adminUser.getAdminUserId());
        return ResultGenerator.genSuccessResult();
    }

}
