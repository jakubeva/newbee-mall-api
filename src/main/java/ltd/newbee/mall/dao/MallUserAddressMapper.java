/**
 * 严肃声明：
 * 开源版本请务必保留此注释头信息，若删除我方将保留所有法律责任追究！
 * 本软件已申请软件著作权，受国家版权局知识产权以及国家计算机软件著作权保护！
 * 可正常分享和学习源码，不得用于违法犯罪活动，违者必究！
 * Copyright (c) 2019-2021 十三 all rights reserved.
 * 版权所有，侵权必究！
 */
package ltd.newbee.mall.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import ltd.newbee.mall.entity.MallUserAddress;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MallUserAddressMapper extends BaseMapper<MallUserAddress> {
    int deleteByPrimaryKey(Long addressId);

    /**
     * insert为全部信息插入，insertSelective为改动选定的信息
     * @param record
     * @return
     */
    int insert(MallUserAddress record);

    int insertSelective(MallUserAddress record);

    MallUserAddress selectByPrimaryKey(Long addressId);

    MallUserAddress getMyDefaultAddress(Long userId);

    List<MallUserAddress> findMyAddressList(Long userId);

    int updateByPrimaryKeySelective(MallUserAddress record);

    int updateByPrimaryKey(MallUserAddress record);
}
