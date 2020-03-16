package com.imooc.o2o.web.shopadmin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.acl.Owner;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imooc.o2o.dto.ShopExecution;
import com.imooc.o2o.entity.Area;
import com.imooc.o2o.entity.PersonInfo;
import com.imooc.o2o.entity.Shop;
import com.imooc.o2o.entity.ShopCategory;
import com.imooc.o2o.enums.ShopStateEnum;
import com.imooc.o2o.exceptions.ShopOperationException;
import com.imooc.o2o.service.AreaService;
import com.imooc.o2o.service.ShopCategoryService;
import com.imooc.o2o.service.ShopService;
import com.imooc.o2o.util.CodeUtil;
import com.imooc.o2o.util.HttpServletRequestUtil;
import com.imooc.o2o.util.ImageUtil;
import com.imooc.o2o.util.PathUtil;
//负责店铺管理相关的逻辑
@Controller
@RequestMapping("/shopadmin")
public class ShopManagementController {
	@Autowired
	private ShopService shopService;//注入ShopService的实现类
	@Autowired 
	private ShopCategoryService ShopCategoryService;//用来返回店铺类别列表
	@Autowired
	private AreaService areaService;//用于接收区域相关的信息
	@RequestMapping(value = "/getshopmanagementinfo",method = RequestMethod.GET)
	@ResponseBody
	private Map<String, Object> getShopManagementInfo(HttpServletRequest request){
		 Map<String, Object> modelMap=new HashMap<String, Object>();
		 long shopId=HttpServletRequestUtil.getLong(request, "shopId");
		 if(shopId<=0) {
			 Object currentShopObj=request.getSession().getAttribute("currentShop");
			 if(currentShopObj==null) {
				 //如果都为空，就表明之前从未进过系统界面，是违规的，需要重定向
				 modelMap.put("redirect", true);
				 modelMap.put("url", "/o2o/shopadmin/shoplist");
			 }else {
				Shop currentShop=(Shop)currentShopObj;
				 modelMap.put("redirect", false);
				 modelMap.put("shopId", currentShop.getShopId());
			}
		 }else {
			Shop currentShop=new Shop();
			currentShop.setShopId(shopId);
			request.getSession().setAttribute("currentShop",currentShop);
			modelMap.put("redirect", false);
		}
		 return modelMap;
	}
	
	@RequestMapping(value = "/getshoplist",method = RequestMethod.GET)
	@ResponseBody
	private Map<String, Object> getShopList(HttpServletRequest request){
		 Map<String, Object> modelMap=new HashMap<String, Object>();
		 PersonInfo user=new PersonInfo();
		 user.setUserId(1L);
		 user.setName("test");
		 request.getSession().setAttribute("user", user);;
		 user=(PersonInfo) request.getSession().getAttribute("user");
		try {
			Shop shopCondition=new Shop();
			shopCondition.setOwner(user);
			ShopExecution se=shopService.getShopList(shopCondition, 0, 100);
			modelMap.put("shopList", se.getShopList());
			modelMap.put("user", user);
			modelMap.put("success", true);
		} catch (Exception e) {
			modelMap.put("success", false);
			modelMap.put("errMsg", e.getMessage());
		}
		return modelMap;
	}
	
	
	@RequestMapping(value = "/getshopbyid",method = RequestMethod.GET)
	@ResponseBody
	private Map<String, Object> getShopById(HttpServletRequest request){
		Map<String, Object> modelMap =new HashMap<String, Object>();//返回值
		Long shopId=HttpServletRequestUtil.getLong(request, "shopId");
		if(shopId>-1) {
			try {
			Shop shop=shopService.getByshoId(shopId);//获取店铺信息，然后返回
			List<Area> areaList=areaService.getAreaList();//获得区域列表
			//hashmap不可重复，无序，适合存店铺各种信息
			modelMap.put("shop", shop);
			modelMap.put("areaList", areaList);	
			modelMap.put("success", true);
			}catch (Exception e) {
				//失败输出错误信息,返回前台
				modelMap.put("success", false);
				modelMap.put("errMsg", e.toString());
			}
		}else {
			modelMap.put("success", false);
			modelMap.put("errMsg", "empty shopId");
		}
		return modelMap;
	}
	
	//这个方法用于获取区域以及店铺类别的信息，并将它返回给前台
	//返回的是json串
	@RequestMapping(value = "/getshopinitinfo",method = RequestMethod.GET)
	@ResponseBody //这个标签将modelMap转换为json串
	private Map<String, Object> getShopInitInfo(){
		Map<String, Object> modelMap=new HashMap<String, Object>();
		List<ShopCategory> shopCategoryList=new ArrayList<ShopCategory>();
		List<Area> areaList=new ArrayList<Area>();
		try {
			//传入new ShopCategory()，获取全部的对象（也就是全部的列表）
			shopCategoryList=ShopCategoryService.getShopCategoryList(new ShopCategory());
			areaList=areaService.getAreaList();
			//成功
			modelMap.put("shopCategoryList", shopCategoryList);
			modelMap.put("areaList", areaList);
			modelMap.put("success", true);
		} catch (Exception e) {
			//失败输出错误信息
			modelMap.put("success", false);
			modelMap.put("errMsg", e.getMessage());
		}
		return modelMap;//返回modelMap
	}
	@RequestMapping(value = "registershop",method = RequestMethod.POST)
	@ResponseBody   //用于将返回值转换为json
	private Map<String, Object> registerShop(HttpServletRequest request){
		Map<String,Object> modelMap=new HashMap<String, Object>();
		if(!CodeUtil.checkVerifyCode(request)) {
			modelMap.put("success", false);
			modelMap.put("errMsg", "输入了错误的验证码");
			return modelMap;
		}
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
			
			//完成session的添加,通过session去获取用户登录的信息
			PersonInfo owner=(PersonInfo)request.getSession().getAttribute("user");
			//将上述获得的店铺信息set到即将添加的owner信息里去 
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
//一个owner可以创建多个店铺，因此需要在session中保存一个店铺列表来显示这个用户可以操作的店铺是哪几个
					@SuppressWarnings("unchecked")
					List<Shop> shopList=(List<Shop>)request.getSession().getAttribute("shopList");
					if(shopList==null||shopList.size()==0) {
						//第一次创建店铺
						shopList=new ArrayList<Shop>();
					}
						shopList.add(se.getShop());
						request.getSession().setAttribute("shopList", shopList);
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

	@RequestMapping(value = "/modifyshop", method = RequestMethod.POST)
	@ResponseBody
	private Map<String, Object> modifyShop(HttpServletRequest request) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		if (!CodeUtil.checkVerifyCode(request)) {
			modelMap.put("success", false);
			modelMap.put("errMsg", "输入了错误的验证码");
			return modelMap;
		}
		// 1.接收并转化相应的参数，包括店铺信息以及图片信息
		String shopStr = HttpServletRequestUtil.getString(request, "shopStr");
		ObjectMapper mapper = new ObjectMapper();
		Shop shop = null;
		try {
			shop = mapper.readValue(shopStr, Shop.class);
		} catch (Exception e) {
			modelMap.put("success", false);
			modelMap.put("errMsg", e.getMessage());
			return modelMap;
		}
		CommonsMultipartFile shopImg = null;
		CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver(
				request.getSession().getServletContext());
		if (commonsMultipartResolver.isMultipart(request)) {
			MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
			shopImg = (CommonsMultipartFile) multipartHttpServletRequest.getFile("shopImg");
		}
		// 2.修改店铺信息
		if (shop != null && shop.getShopId() != null) {
			ShopExecution se;
			try {
				if (shopImg == null) {
					se = shopService.modifyShop(shop, null,null);
				} else {
					se = shopService.modifyShop(shop, shopImg.getInputStream(),shopImg.getOriginalFilename());
				}
				if (se.getState() == ShopStateEnum.SUCCESS.getState()) {
					modelMap.put("success", true);
				} else {
					modelMap.put("success", false);
					modelMap.put("errMsg", se.getStateInfo());
				}
			} catch (ShopOperationException e) {
				modelMap.put("success", false);
				modelMap.put("errMsg", e.getMessage());
			} catch (IOException e) {
				modelMap.put("success", false);
				modelMap.put("errMsg", e.getMessage());
			}
			return modelMap;
		} else {
			modelMap.put("success", false);
			modelMap.put("errMsg", "请输入店铺Id");
			return modelMap;
		}
	}
}
