package com.imooc.o2o.web.superadmin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.imooc.o2o.entity.Area;
import com.imooc.o2o.service.AreaService;


@Controller//作为标签
@RequestMapping("/superadmin")	//路由作用

public class AreaController {
	Logger logger=LoggerFactory.getLogger(AreaController.class);
	
	@Autowired
	private AreaService areaservice;//在程序用到AreaService时，将它的实现类自动注入
	@RequestMapping(value="/listarea",method = RequestMethod.GET)
	
	@ResponseBody   
	//用来告诉对象get到的数据要自动转换为json
	private Map<String,Object> listArea(){//函数用于列出区域列表
		logger.info("===start===");
		long startTime=System.currentTimeMillis();
		
		Map<String,Object> modeleMap=new HashMap<String, Object>();//用来存放方法的返回值
		List<Area> list=new ArrayList<Area>();//用来获取service层返回的区域列表
		try {
			list=areaservice.getAreaList();
			modeleMap.put("rows", list);
			modeleMap.put("total", list.size());
		}catch (Exception e) {
			e.printStackTrace();
			modeleMap.put("success", false);
			modeleMap.put("errMsg", e.toString());
		}
		logger.error("test error!");
		long endTime=System.currentTimeMillis();
		logger.debug("costTime[{}ms]",endTime-startTime);
		logger.info("===end===");
		return modeleMap;
	}
	
	
}
