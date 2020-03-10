package com.imooc.o2o.dao;

import java.util.List;

import com.imooc.o2o.entity.Area;
/*
 * 列出区域列表，返回areaList
 * */
public interface AreaDao {
	
	List<Area> queryArea();//通过DAO查询的接口
}
