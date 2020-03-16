package com.imooc.o2o.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.imooc.o2o.entity.Shop;

public interface ShopDao {
	
	/*
	 * 分页查询店铺，可输入的条件有：店铺名（模糊），店铺状态，店铺类别区域Id，owner
	 * rowIndex表示从第几行取数据，从第一行
	 * pageSize表示要返回多少行数据，取五行，即：5+1
	 * 
	 * 因为参数有多个，所以要用@Param去通过唯一标识取数据
	 * */
	List<Shop> queryShopList(@Param("shopCondition")Shop shopCondition,
			@Param("rowIndex")int rowIndex,@Param("pageSize")int pageSize);
	/*
	 * 返回queryList总数
	 * */
	int queryShopCount(@Param("shopCondition")Shop shopCondition);
	
	/*
	 * 新增店铺
	 * */
	int insertShop(Shop shop);
	/*
	 * 通过shop_id进行查询店铺
	 * */
	Shop queryByShopId(long shopId);
	
	/*
	 
	 * 更新店铺信息
	 * */
	int updateShop(Shop shop);
}
