/**
 * 严肃声明：
 * 开源版本请务必保留此注释头信息，若删除我方将保留所有法律责任追究！
 * 本软件已申请软件著作权，受国家版权局知识产权以及国家计算机软件著作权保护！
 * 可正常分享和学习源码，不得用于违法犯罪活动，违者必究！
 * Copyright (c) 2019-2021 十三 all rights reserved.
 * 版权所有，侵权必究！
 */
package ltd.newbee.mall.service.impl;

import ltd.newbee.mall.api.mall.vo.NewBeeMallIndexCategoryVO;
import ltd.newbee.mall.api.mall.vo.SecondLevelCategoryVO;
import ltd.newbee.mall.api.mall.vo.ThirdLevelCategoryVO;
import ltd.newbee.mall.common.Constants;
import ltd.newbee.mall.common.NewBeeMallCategoryLevelEnum;
import ltd.newbee.mall.common.ServiceResultEnum;
import ltd.newbee.mall.dao.GoodsCategoryMapper;
import ltd.newbee.mall.entity.GoodsCategory;
import ltd.newbee.mall.service.NewBeeMallCategoryService;
import ltd.newbee.mall.util.BeanUtil;
import ltd.newbee.mall.util.PageQueryUtil;
import ltd.newbee.mall.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

/**
 * 该类为分类业务逻辑的实现
 *
 * @author 13
 */
@Service
public class NewBeeMallCategoryServiceImpl implements NewBeeMallCategoryService {

    @Autowired
    private GoodsCategoryMapper goodsCategoryMapper;

    /**
     * 新增分类
     * @param goodsCategory
     * @return
     */
    @Override
    public String saveCategory(GoodsCategory goodsCategory) {
        /**
         * 先从数据库中查询该级别和名称的分类是否存在，如果已经存在就返回“已存在同级同名的分类”
         */
        GoodsCategory temp = goodsCategoryMapper.selectByLevelAndName(goodsCategory.getCategoryLevel(), goodsCategory.getCategoryName());
        if (temp != null) {
            return ServiceResultEnum.SAME_CATEGORY_EXIST.getResult();
        }
        /**
         * 如果分类新增成功，返回success，否则返回数据库error
         */
        if (goodsCategoryMapper.insertSelective(goodsCategory) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    /**
     * 修改分类
     * @param goodsCategory
     * @return
     */
    @Override
    public String updateGoodsCategory(GoodsCategory goodsCategory) {
        //根据id查找分类
        GoodsCategory temp = goodsCategoryMapper.selectByPrimaryKey(goodsCategory.getCategoryId());
        /**
         * 如果分类为空，返回操作结果为数据不存在
         */
        if (temp == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        /**
         * 如果存在同级同名但不同id的分类，返回错误
         */
        GoodsCategory temp2 = goodsCategoryMapper.selectByLevelAndName(goodsCategory.getCategoryLevel(), goodsCategory.getCategoryName());
        if (temp2 != null && !temp2.getCategoryId().equals(goodsCategory.getCategoryId())) {
            //同名且不同id 不能继续修改
            return ServiceResultEnum.SAME_CATEGORY_EXIST.getResult();
        }
        /**
         * 更新修改时间
         */
        goodsCategory.setUpdateTime(new Date());
        /**
         * 如果更新成功，返回success，否则返回数据库error
         */
        if (goodsCategoryMapper.updateByPrimaryKeySelective(goodsCategory) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    /**
     * 根据id返回分类
     * @param id
     * @return
     */
    @Override
    public GoodsCategory getGoodsCategoryById(Long id) {
        return goodsCategoryMapper.selectByPrimaryKey(id);
    }

    /**
     * 批量删除
     * @param ids
     * @return
     */
    @Override
    public Boolean deleteBatch(Long[] ids) {
        if (ids.length < 1) {
            return false;
        }
        //删除分类数据
        return goodsCategoryMapper.deleteBatch(ids) > 0;
    }

    /**
     * 返回分类数据(首页调用)
     *
     * @return
     */
    @Override
    public List<NewBeeMallIndexCategoryVO> getCategoriesForIndex() {
        List<NewBeeMallIndexCategoryVO> newBeeMallIndexCategoryVOS = new ArrayList<>();
        //获取一级分类的固定数量的数据
        List<GoodsCategory> firstLevelCategories = goodsCategoryMapper.selectByLevelAndParentIdsAndNumber(Collections.singletonList(0L), NewBeeMallCategoryLevelEnum.LEVEL_ONE.getLevel(), Constants.INDEX_CATEGORY_NUMBER);
        /**
         * 如果一级分类列表不为空
         */
        if (!CollectionUtils.isEmpty(firstLevelCategories)) {
            //获取一级分类的id
            List<Long> firstLevelCategoryIds = firstLevelCategories.stream().map(GoodsCategory::getCategoryId).collect(Collectors.toList());
            //获取二级分类的数据
            List<GoodsCategory> secondLevelCategories = goodsCategoryMapper.selectByLevelAndParentIdsAndNumber(firstLevelCategoryIds, NewBeeMallCategoryLevelEnum.LEVEL_TWO.getLevel(), 0);
            if (!CollectionUtils.isEmpty(secondLevelCategories)) {
                //获取二级分类的id
                List<Long> secondLevelCategoryIds = secondLevelCategories.stream().map(GoodsCategory::getCategoryId).collect(Collectors.toList());
                //获取三级分类的数据
                List<GoodsCategory> thirdLevelCategories = goodsCategoryMapper.selectByLevelAndParentIdsAndNumber(secondLevelCategoryIds, NewBeeMallCategoryLevelEnum.LEVEL_THREE.getLevel(), 0);
                if (!CollectionUtils.isEmpty(thirdLevelCategories)) {
                    /**
                     * 根据 parentId 将 thirdLevelCategories 分组
                     */
                    Map<Long, List<GoodsCategory>> thirdLevelCategoryMap = thirdLevelCategories.stream().collect(groupingBy(GoodsCategory::getParentId));
                    List<SecondLevelCategoryVO> secondLevelCategoryVOS = new ArrayList<>();
                    //处理二级分类
                    /**
                     * 对每一个二级分类，都将实体类转换为VO类
                     */
                    for (GoodsCategory secondLevelCategory : secondLevelCategories) {
                        SecondLevelCategoryVO secondLevelCategoryVO = new SecondLevelCategoryVO();
                        BeanUtil.copyProperties(secondLevelCategory, secondLevelCategoryVO);
                        /**
                         * 如果该二级分类下有数据则放入 secondLevelCategoryVOS 对象中
                         */
                        if (thirdLevelCategoryMap.containsKey(secondLevelCategory.getCategoryId())) {
                            /**
                             * 根据二级分类的id取出thirdLevelCategoryMap分组中的三级分类list
                             */
                            List<GoodsCategory> tempGoodsCategories = thirdLevelCategoryMap.get(secondLevelCategory.getCategoryId());
                            secondLevelCategoryVO.setThirdLevelCategoryVOS((BeanUtil.copyList(tempGoodsCategories, ThirdLevelCategoryVO.class)));
                            secondLevelCategoryVOS.add(secondLevelCategoryVO);
                        }
                    }
                    /**
                     * 处理一级分类
                     */
                    if (!CollectionUtils.isEmpty(secondLevelCategoryVOS)) {
                        /**
                         * 根据 parentId 将 secondLevelCategoryVO 分组
                         */
                        Map<Long, List<SecondLevelCategoryVO>> secondLevelCategoryVOMap = secondLevelCategoryVOS.stream().collect(groupingBy(SecondLevelCategoryVO::getParentId));
                        /**
                         * 对每一个一级分类，都将实体类转换为VO类
                         */
                        for (GoodsCategory firstCategory : firstLevelCategories) {
                            NewBeeMallIndexCategoryVO newBeeMallIndexCategoryVO = new NewBeeMallIndexCategoryVO();
                            BeanUtil.copyProperties(firstCategory, newBeeMallIndexCategoryVO);
                            /**
                             * 如果该一级分类下有数据则放入 newBeeMallIndexCategoryVOS 对象中
                             */
                            if (secondLevelCategoryVOMap.containsKey(firstCategory.getCategoryId())) {
                                /**
                                 * 根据一级分类的id取出secondLevelCategoryVOMap分组中的二级级分类list
                                 */
                                List<SecondLevelCategoryVO> tempGoodsCategories = secondLevelCategoryVOMap.get(firstCategory.getCategoryId());
                                newBeeMallIndexCategoryVO.setSecondLevelCategoryVOS(tempGoodsCategories);
                                newBeeMallIndexCategoryVOS.add(newBeeMallIndexCategoryVO);
                            }
                        }
                    }
                }
            }
            return newBeeMallIndexCategoryVOS;
        } else {
            return null;
        }
    }

    /**
     * 后台分页
     *
     * @param pageUtil
     * @return
     */
    @Override
    public PageResult getCategorisPage(PageQueryUtil pageUtil) {
        List<GoodsCategory> goodsCategories = goodsCategoryMapper.findGoodsCategoryList(pageUtil);
        int total = goodsCategoryMapper.getTotalGoodsCategories(pageUtil);
        PageResult pageResult = new PageResult(goodsCategories, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    /**
     * 根据parentId和level获取分类列表
     *
     * @param parentIds
     * @param categoryLevel
     * @return
     */
    @Override
    public List<GoodsCategory> selectByLevelAndParentIdsAndNumber(List<Long> parentIds, int categoryLevel) {
        return goodsCategoryMapper.selectByLevelAndParentIdsAndNumber(parentIds, categoryLevel, 0);//0代表查询所有
    }
}
