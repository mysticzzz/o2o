package com.imooc.o2o.web.shopadmin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.acl.Owner;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imooc.o2o.dto.ShopExecution;
import com.imooc.o2o.entity.PersonInfo;
import com.imooc.o2o.entity.Shop;
import com.imooc.o2o.enums.ShopStateEnum;
import com.imooc.o2o.exceptions.ShopOperationException;
import com.imooc.o2o.service.ShopService;
import com.imooc.o2o.util.HttpServletRequestUtil;
import com.imooc.o2o.util.ImageUtil;
import com.imooc.o2o.util.PathUtil;
//负责店铺管理相关的逻辑
@Controller
@RequestMapping("/shopadmin")
public class ShopManagementController {
	@Autowired
	private ShopService shopService;//注入ShopService的实现类
	@RequestMapping(value = "registershop",method = RequestMethod.POST)
	@ResponseBody   //用于将返回值转换为json
	private Map<String, Object> registerShop(HttpServletRequest request){
		Map<String,Object> modelMap=new HashMap<String, Object>();
		//1.接收并转换相应的参数，包括店铺信息以及图片信息
		String shopStr=HttpServletRequestUtil.getString(request, "shopStr");
		
		ObjectMapper mapper=new ObjectMapper();
		Shop shop=null;
		try {
			//通过jkson将它转为实体类
			shop=mapper.readValue(shopStr, Shop.class);//转换为Shop实体类
		}catch (Exception e) {
			modelMap.put("success", false);
			modelMap.put("errMsg", e.getMessage());
			return modelMap;
		}
		CommonsMultipartFile shopImg=null;
		CommonsMultipartResolver commonsMultipartResolver=new CommonsMultipartResolver(
				request.getSession().getServletContext());
		//判断是否有上传的文件流
		if(commonsMultipartResolver.isMultipart(request)) {
			//获取前端传过来的文件流，将它接收到shopImg里面去
			MultipartHttpServletRequest multipartHttpServletRequest=(MultipartHttpServletRequest)request;
			shopImg=(CommonsMultipartFile) multipartHttpServletRequest.getFile("shopImg");
			
		}
		else {
			modelMap.put("success", false);
			modelMap.put("errMsg", "上传图片不能为空");
			return modelMap;
		}
		//2.注册店铺
		if(shop!=null&&shopImg!=null) {
			PersonInfo owner=new PersonInfo();
			//Session TODO
			owner.setUserId(1L);
			shop.setOwner(owner);
//			//获得一个随机的新的文件
//			File shopImgFile=new File(PathUtil.getImgBasePath()+ImageUtil.getRandomFileName());
//			try {
//				shopImgFile.createNewFile();
//			} catch (IOException e) {
//				modelMap.put("success", false);
//				modelMap.put("errMsg", e.getMessage());
//				return modelMap;
//			}
//			
			ShopExecution se;
			try {
				se = shopService.addShop(shop, shopImg.getInputStream(),shopImg.getOriginalFilename());
				if(se.getState()==ShopStateEnum.CHECK.getState()) {
					modelMap.put("success", true);
				}else {
					modelMap.put("success", false);
					modelMap.put("errMsg", se.getStateInfo());//返回状态对应的注释
					
				}
			} catch (ShopOperationException e) {
				
				modelMap.put("success", false);
				modelMap.put("errMsg", e.getMessage());
			} catch (IOException e) {
				modelMap.put("success", false);
				modelMap.put("errMsg", e.getMessage());
			}
			
			return modelMap;
		}else {
			modelMap.put("success", false);
			modelMap.put("errMsg", "请输入店铺信息");
			return modelMap;
		}
		//3.返回结果(穿插在try catch块里，故这步省略了)
		
		
	}
	//将InputStream类型转换为File类型传入shopService.addShop方法，便于单元测试
//	private static void InputStreamToFile(InputStream ins,File file) {
//		FileOutputStream os=null;
//		try {
//			os=new FileOutputStream(file);
//			int bytesRead=0;
//			byte[] buffer=new byte[1024]; //用buffer去读入inputstream里面的内容
//			while((bytesRead=ins.read(buffer))!=-1) {
//				//读满1024个字节，就往输出流里写入一次,直到读完位置
//				os.write(buffer,0,bytesRead);
//			}
//		} catch (Exception e) {
//			throw new RuntimeException("调用inputStreamToFile产生异常: "+e.getMessage());
//		} finally {//关闭输入输出流
//			try {
//				if(os!=null) {
//					os.close();
//				}
//				if(ins!=null) {
//					ins.close();
//				}
//			}catch (IOException e ) {
//				throw new RuntimeException("调用inputStreamToFile关闭io异常: "+e.getMessage());
//			}
//		}
//	}
}
