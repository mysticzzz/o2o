package com.imooc.o2o.util;

import javax.servlet.http.HttpServletRequest;

//用于处理HttpRequest参数
public class HttpServletRequestUtil {
	//从http传入(request里)的key中取出需要的数据，并转成需要的类型
	public static int getInt(HttpServletRequest request,String key) {
		 try {
			 //request.getParameter返回的是字符串的对象
			 return Integer.decode(request.getParameter(key));//字符串转换为整形
		 }catch (Exception e) {
			return -1;
		}
	}
	
	public static long getLong(HttpServletRequest request,String key) {
	 try {
		 //request.getParameter返回的是字符串的对象
		 return Long.valueOf(request.getParameter(key));//字符串转换为整形
	 }catch (Exception e) {
		return -1;
	}
}
	public static Double getDouble(HttpServletRequest request, String key) {
		try {
			return Double.valueOf(request.getParameter(key));
		} catch (Exception e) {
			return -1d;
		}
	}

	public static boolean getBoolean(HttpServletRequest request, String key) {
		try {
			return Boolean.valueOf(request.getParameter(key));
		} catch (Exception e) {
			return false;
		}
	}
	
	public static String getString(HttpServletRequest request, String key) {
			try {
				String result = request.getParameter(key);
				if (result != null) {
					result = result.trim();//去掉两侧的空格
				}
				if ("".equals(result)) {
					result = null;
				}
				return result;
			} catch (Exception e) {
				return null;
			}
		}
	
}
