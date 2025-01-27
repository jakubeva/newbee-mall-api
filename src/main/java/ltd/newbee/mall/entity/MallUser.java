/**
 * 严肃声明：
 * 开源版本请务必保留此注释头信息，若删除我方将保留所有法律责任追究！
 * 本软件已申请软件著作权，受国家版权局知识产权以及国家计算机软件著作权保护！
 * 可正常分享和学习源码，不得用于违法犯罪活动，违者必究！
 * Copyright (c) 2019-2021 十三 all rights reserved.
 * 版权所有，侵权必究！
 */
package ltd.newbee.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
@TableName("tb_newbee_mall_user")
public class MallUser {
    /**
     * 用户id
     */
    @TableId(value = "user_id", type = IdType.AUTO)
    protected Long userId;
    /**
     * 用户昵称
     */
    protected String nickName;
    /**
     * 用户登录名
     */
    protected String loginName;
    /**
     * Md5加密的密码
     */
    protected String passwordMd5;
    /**
     * 用户个性签名
     */
    protected String introduceSign;
    /**
     * 是否被删除的标记位
     */
    @TableLogic(value = "0", delval = "1")
    protected Byte isDeleted;
    /**
     *
     */
    protected Byte lockedFlag;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    protected Date createTime;
}
