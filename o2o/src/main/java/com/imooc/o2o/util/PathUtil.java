package com.imooc.o2o.util;

public class PathUtil {
	//获取文件的分隔符
	private static String seperator=System.getProperty("file.separator");
	//返回项目图片的根路径
	public static String getImgBasePath() {
		String os=System.getProperty("os.name");
		String basePath="";
		if(os.toLowerCase().startsWith("win")) {
			basePath="D:/picture/";
		}else {
			basePath="/home/ZC/image/";
		}
		basePath=basePath.replace("/", seperator);//替换basePath的斜杠
		return basePath;
	}
	//根据不同的业务需求返回项目图片的子路径
	public static String getShopImagePath(long shopId) {
		String imagePath="upload/item/shop/"+shopId+"/";
		return imagePath.replace("/", seperator);
	}
	
}
