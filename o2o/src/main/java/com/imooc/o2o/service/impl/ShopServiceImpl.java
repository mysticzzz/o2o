package com.imooc.o2o.service.impl;

import java.io.File;
import java.io.InputStream;
import java.util.Date;

import javax.management.RuntimeErrorException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.imooc.o2o.dao.ShopDao;
import com.imooc.o2o.dto.ShopExecution;
import com.imooc.o2o.entity.Shop;
import com.imooc.o2o.enums.ShopStateEnum;
import com.imooc.o2o.exceptions.ShopOperationException;
import com.imooc.o2o.service.ShopService;
import com.imooc.o2o.util.ImageUtil;
import com.imooc.o2o.util.PathUtil;
@Service
public class ShopServiceImpl implements ShopService{
	@Autowired
	private ShopDao shopDao;
	@Override
	@Transactional
	public ShopExecution addShop(Shop shop,InputStream shopImgIputInputStream,String fileName) {
		
		//判断shop是否为空（空值判断）
		if(shop==null) {
			return new ShopExecution(ShopStateEnum.NULL_SHOP);
		}
		try {
			//给店铺信息赋初始值
			shop.setEnableStatus(0);
			shop.setCreateTime(new Date());
			shop.setLastEditTime(new Date());
			//添加店铺信息
			int effectedNum=shopDao.insertShop(shop);//插入
			if(effectedNum<=0) {
				throw new ShopOperationException("店铺创建失败");//事务得以终止并回滚
			}else {
				if(shopImgIputInputStream!=null) {
					/*
					 * //存储图片 //利用shop里面的id去存储目录，然后存入shopImg这个文件流，存储到文件目录中 addShopImg(shop,shopImg);
					 * //存储成功后，这个方法就会将生成好的图片地址更新到shop实体类中，在外面调用 shop.getShopImg();//就可以得到图片的地址
					 */				
					try {
						addShopImg(shop,shopImgIputInputStream,fileName);
					}catch (Exception e) {
						throw new ShopOperationException("addShopImg error:"+e.getMessage());
					}
					//更新店铺的图片地址
					effectedNum=shopDao.updateShop(shop);
					if(effectedNum<=0) {
						throw new ShopOperationException("更新图片地址失败");
					}
				}
			}

		}catch (Exception e) {
			throw new ShopOperationException("addShop error:"+e.getMessage());
		}
		return new ShopExecution(ShopStateEnum.CHECK,shop);
	}
	private void addShopImg(Shop shop, InputStream shopImgInputStream,String fileName) {
		// 获取shop图片目录的相对值路径
		String dest=PathUtil.getShopImagePath(shop.getShopId());
		//调用方法去存储图片并返回相应的相对值路径
		String shopImgAddr=ImageUtil.generateThumbnail(shopImgInputStream, fileName,dest);
		//generateThumbnail将根路径和传入的相对值路径结合，进而传出图片实际存储的绝对值路径
		//得出绝对值路径以后，将图片存入，再返回图片的相对值路径，以便更新到数据库内
		shop.setShopImg(shopImgAddr);
	}
}
