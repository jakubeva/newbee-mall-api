/**
 * 严肃声明：
 * 开源版本请务必保留此注释头信息，若删除我方将保留所有法律责任追究！
 * 本软件已申请软件著作权，受国家版权局知识产权以及国家计算机软件著作权保护！
 * 可正常分享和学习源码，不得用于违法犯罪活动，违者必究！
 * Copyright (c) 2019-2021 十三 all rights reserved.
 * 版权所有，侵权必究！
 */
package ltd.newbee.mall.api.mall.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 修改购物项param
 */
@Data
public class UpdateCartItemParam implements Serializable {

    /**
     * 购物车中要修改的购物项id
     */
    @ApiModelProperty("购物项id")
    private Long cartItemId;

    /**
     * 购物车中定义购物项中的商品数量
     */

    @ApiModelProperty("商品数量")
    private Integer goodsCount;
}
