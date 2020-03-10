package com.imooc.o2o.exceptions;

public class ShopOperationException extends RuntimeException{
	/**
	 * 生成随机化的ID
	 * 
	 */
	private static final long serialVersionUID = 2361446884822298905L;

	public ShopOperationException(String msg) {
		super(msg);//super表示RuntimeException的构造函数，接收的事error message
	}
}
