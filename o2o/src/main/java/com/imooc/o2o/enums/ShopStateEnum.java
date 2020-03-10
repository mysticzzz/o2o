package com.imooc.o2o.enums;

public enum ShopStateEnum {
	CHECK(0,"审核中"),OFFLINE(-1,"非法店铺"),SUCCESS(1,"操作成功"),
	PASS(2,"通过认证"),INNER_ERROR(-10001,"内部系统错误"),
	NULL_SHOPID(-1002,"ShopId为空"),NULL_SHOP(-1003,"shop信息为空");
	
	private int state;
	private String stateInfo;
	//构造器，不希望第三方程序去改变上述的值，所以定义为私有构造
	private ShopStateEnum(int state,String sateInfo) {
		this.state=state;
		this.stateInfo=sateInfo;
	}
	
	public int getState() {
		return state;
	}

	public String getStateInfo() {
		return stateInfo;
	}

	/*
	 * 依据传入的state返回相应的enum值
	 * */
	public static ShopStateEnum stateOf(int state) {
		for(ShopStateEnum stateEnum:values()) {
			if(stateEnum.getState()==state) {
				return stateEnum;
			}
		}
		return null;
	}
	
	
}
