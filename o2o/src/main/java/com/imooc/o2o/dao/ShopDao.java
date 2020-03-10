package com.imooc.o2o.dao;

import com.imooc.o2o.entity.Shop;

public interface ShopDao {
	/*
	 * 新增店铺
	 * */
	int insertShop(Shop shop);
	/*
	 * 更新店铺信息
	 * */
	int updateShop(Shop shop);
}
