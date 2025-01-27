/**
 * 严肃声明：
 * 开源版本请务必保留此注释头信息，若删除我方将保留所有法律责任追究！
 * 本软件已申请软件著作权，受国家版权局知识产权以及国家计算机软件著作权保护！
 * 可正常分享和学习源码，不得用于违法犯罪活动，违者必究！
 * Copyright (c) 2019-2021 十三 all rights reserved.
 * 版权所有，侵权必究！
 */
package ltd.newbee.mall.service;

import ltd.newbee.mall.api.mall.vo.NewBeeMallIndexCarouselVO;
import ltd.newbee.mall.entity.Carousel;
import ltd.newbee.mall.util.PageQueryUtil;
import ltd.newbee.mall.util.PageResult;

import java.util.List;

/**
 * 该类为轮播图业务层接口
 *
 * @author 十三
 */
public interface NewBeeMallCarouselService {

    /**
     * 返回固定数量的轮播图对象(首页调用)
     *
     * @param number
     * @return
     */
    List<NewBeeMallIndexCarouselVO> getCarouselsForIndex(int number);

    /**
     * 后台分页
     *
     * @param pageUtil
     * @return
     */
    PageResult getCarouselPage(PageQueryUtil pageUtil);

    /**
     * 新增轮播图
     * @param carousel
     * @return 操作结果是否成功
     */
    String saveCarousel(Carousel carousel);

    /**
     * 修改轮播图信息
     * @param carousel
     * @return 操作结果是否成功
     */
    String updateCarousel(Carousel carousel);

    /**
     * 根据id返回轮播图
     * @param id
     * @return 轮播图
     */
    Carousel getCarouselById(Integer id);

    /**
     * 批量删除轮播图
     * @param ids
     * @return 删除结果
     */
    Boolean deleteBatch(Long[] ids);
}
