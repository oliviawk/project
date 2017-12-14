package com.cn.hitec.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Random;

import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName: Tools
 * @Description: TODO(其他工具类)
 * @author ZhangLu
 * @date 2017年6月5日 下午2:26:32
 * 
 */
@Slf4j
public class Tools {

	public static String readTxtFile(String filePath) {
		StringBuffer strBuff = new StringBuffer();
		try {
			String encoding = "utf-8";
			File file = new File(filePath);
			if (file.isFile() && file.exists()) { // 判断文件是否存在
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);// 考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					strBuff.append(lineTxt);
				}
				read.close();
			} else {
				log.error("找不到指定的文件");
			}
			return strBuff.toString();
		} catch (Exception e) {
			log.error("读取文件内容出错 Message:"+e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取一个指定范围内的随机数
	 * 
	 * @param min
	 *            最小随机数
	 * @param max
	 *            最大随机数
	 * @author ZhangLu 2017年6月6日09:14:32
	 * @return
	 */
	public static int getRandom(int min, int max) {
		Random random = new Random();
		int randomNum = random.nextInt(max) % (max - min + 1) + min;
		return randomNum;
	}

//	public static void main(String argv[]) {
//		String filePath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\data\\ProductCompleteMonitor.json";
//		readTxtFile(filePath);
//		System.out.println(filePath);
//	}
}
