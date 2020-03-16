package com.imooc.o2o.service;

import java.io.File;
import java.io.InputStream;

import com.imooc.o2o.dto.ShopExecution;
import com.imooc.o2o.entity.Shop;
import com.imooc.o2o.exceptions.ShopOperationException;

public interface ShopService {
	/*
	 * 根据shopCondition分页返回相应列表数据
	 * */
	public ShopExecution getShopList(Shop shopCondition,int pageIndex,int pageSize);
	/*
	 * 店铺注册
	 * */
	ShopExecution addShop(Shop shop,InputStream shopImgInputStream,String fileName) throws ShopOperationException;
	/*
	 * 根据Id获取店铺信息
	 * */
	Shop getByshoId(long shopId) ;
	/*
	 * 更新店铺信息，包括对图片的处理
	 * */
	ShopExecution modifyShop(Shop shop,InputStream shopImgInputStream,String fileName) throws ShopOperationException;
	
}
