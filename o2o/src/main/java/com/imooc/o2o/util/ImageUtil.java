package com.imooc.o2o.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import javax.imageio.ImageIO;

import org.apache.ibatis.javassist.expr.NewArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Position;
import net.coobird.thumbnailator.geometry.Positions;

public class ImageUtil {
	private static String basePath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
	private static final SimpleDateFormat sDateFormat=new SimpleDateFormat("yyyyMMddHHmmss");
	private static final Random r=new Random();
	private static Logger logger=LoggerFactory.getLogger(ImageUtil.class);
	//将CommonsMultipartFile转化成File
	public static File transferCommonsMultipartFileToFile(CommonsMultipartFile cFile) {
		File newFile=new File(cFile.getOriginalFilename());
		try {
			cFile.transferTo(newFile);
		} catch (IllegalStateException e) {
			logger.error(e.toString());
			e.printStackTrace();
		} catch (IOException e) {
			logger.error(e.toString());
			e.printStackTrace();
		}
		return newFile;
		
	}
	/*处理缩略图，并返回新生成图片的相对值路径*/
	public static String generateThumbnail(InputStream thumbnailInputStream,String fileName,String targetAddr) {
		String realFileName=getRandomFileName();//随机名
		String extension=getFileExtension(fileName);//扩展名
		makeDirPath(targetAddr);
		String relativeAddr=targetAddr+realFileName+extension;
		logger.debug("current relativeAddr is:"+relativeAddr);
		//新生成的文件路径
		File dest=new File(PathUtil.getImgBasePath()+relativeAddr);
		logger.debug("current complete addr is:"+PathUtil.getImgBasePath()+relativeAddr);
		try {
			Thumbnails.of(thumbnailInputStream).size(200, 200)
			.watermark(Positions.BOTTOM_RIGHT, ImageIO.read(new File(basePath + "/watermark.jpg")), 0.25f)
			.outputQuality(0.8f).toFile(dest);
		}catch (IOException e) {
			logger.error(extension.toString());
			e.printStackTrace();
		}
		//返回相对地址，而不是绝对地址，希望项目迁移以后也能正常使用，达到解耦的功能
		return relativeAddr;
		
	}
	//创建目标路径所涉及到的目录，即/home/work/ZC/xxx.jpg,
	//那么home work ZC这三个文件都得自动创建
	private static void makeDirPath(String targetAddr) {
		//目标文件所属的全路径
		String realFileParentPath=PathUtil.getImgBasePath()+targetAddr;
		File dirPath=new File(realFileParentPath);
		if(!dirPath.exists()) {
			dirPath.mkdirs();
		}
	}
	//获取输入文件流的扩展名
	private static String getFileExtension(String fileName) {
		
		return fileName.substring(fileName.lastIndexOf("."));
	}
	//生成随机的文件，要求每个文件的名字都不同
	//当前年月日小时分钟秒钟+五位随机数
	public static String getRandomFileName() {
		//获取随机的五位数
		int rannum=r.nextInt(89999)+10000;
		String nowTimeStr=sDateFormat.format(new Date());
		return nowTimeStr+rannum;
	}
	public static void main(String[] args) throws IOException {
		Thumbnails.of(new File("C:/Users/17251/Desktop/picture/nier.jpg")).size(200, 200)
				.watermark(Positions.BOTTOM_RIGHT, ImageIO.read(new File(basePath + "/watermark.jpg")), 0.25f)
				.outputQuality(0.8f).toFile("C:/Users/17251/Desktop/picture/niernew.jpg");
	}
}
