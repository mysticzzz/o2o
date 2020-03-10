package com.immoc.o2o.service;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.imooc.o2o.BaseTest;
import com.imooc.o2o.dto.ShopExecution;
import com.imooc.o2o.entity.Area;
import com.imooc.o2o.entity.PersonInfo;
import com.imooc.o2o.entity.Shop;
import com.imooc.o2o.entity.ShopCategory;
import com.imooc.o2o.enums.ShopStateEnum;
import com.imooc.o2o.service.ShopService;

public class ShopServiceTest extends BaseTest{
	//BaseTest主要是用于初始化Spring容器的，在它里面就会有一个ShopService接口
	//@Autowired是告诉spring，在初始化这个SHopServiceTest的时候
	//就将ShopService的实现类动态的注入到接口里面去
	@Autowired
	private ShopService shopService;//接口
	
	@Test
	public void testAddShop() throws FileNotFoundException {
		//初始化实例
		Shop shop = new Shop();
		PersonInfo owner = new PersonInfo();
		Area area = new Area();
		ShopCategory shopCategory = new ShopCategory();
		owner.setUserId(1L);
		area.setAreaId(2);
		shopCategory.setShopCategoryId(1L);
		shop.setOwner(owner);
		shop.setArea(area);
		shop.setShopCategory(shopCategory);
		shop.setShopName("测试的店铺3");
		shop.setShopDesc("test3");
		shop.setShopAddr("test3");
		shop.setPhone("test3");
		shop.setCreateTime(new Date());
		shop.setEnableStatus(ShopStateEnum.CHECK.getState());
		shop.setAdvice("审核中");
		//初始化一个文件
		File shopImg=new File("D:/picture/nier.jpg");
		//将File类型转换为InputStream
		InputStream is=new FileInputStream(shopImg);
		//开始作添加
		ShopExecution se=shopService.addShop(shop, is,shopImg.getName());
		assertEquals(ShopStateEnum.CHECK.getState(),se.getState());
		//若添加失败则会回滚，但是如果ShopOperationException是继承Exception
		//而不是RuntimeException，则不会回滚，添加错误的信息去数据库表。
	}
	
	
}
