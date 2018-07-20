package com.hitec.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.crypto.Data;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * @ClassName: Tools
 * @Description: TODO(其他工具类)
 * @author ZhangLu
 * @date 2017年6月5日 下午2:26:32
 * 
 */
public class Tools {
	private static final Logger logger = LoggerFactory.getLogger(Tools.class);

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
				logger.error("找不到指定的文件");
			}
			return strBuff.toString();
		} catch (Exception e) {
			logger.error("读取文件内容出错 Message:" + e.getMessage());
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

	/**
	 * isNull:(判断一个Object的变量是否为空) 如果变量的引用值为 null/""/"null" 则返回true 否则返回false
	 * 
	 * @author ZhangLu 2017年9月22日13:03:08
	 * @param str
	 * @return
	 */
	public static boolean isNull(Object str) {
		boolean isNull = false;
		try {
			if (null == str || "".equals(str) || "null" == str)
				isNull = true;
		} catch (Exception e) {
			logger.error("判断字符是否为空出现错误,判断的字符为：" + str + ",错误为：" + e.getMessage());
			e.printStackTrace();
		}
		return isNull;
	}

	public static void main(String argv[]) {
//		String filePath = System.getProperty("user.dir")
//				+ "\\src\\main\\resources\\static\\data\\ProductCompleteMonitor.json";
//		readTxtFile(filePath);
//		System.out.println(filePath);

//		String strFormat = "yy-MM-dd";
//		SimpleDateFormat sdf = new SimpleDateFormat(strFormat);
//		String strData = sdf.format(new Date());
//		System.out.println(strData);



	}

	/**
	 * bubbleSort:(冒泡排序,将从cassandra数据库中查询的结果进行冒泡排序,时间小的在前,大的在后)
	 * 
	 * @author ZhangLu 2017年9月27日10:43:08
	 * @param jsonStr
	 *            查询出来的结果集字符串
	 * @return
	 */
	public static JSONArray bubbleSort(String jsonStr) {
		JSONArray pointsArr = new JSONArray();
		try {
			JSONArray resultArray = JSONArray.parseArray(jsonStr);
			JSONObject resultObj = resultArray.getJSONObject(0);
			if (!resultObj.containsKey("points")) {
				logger.error("格式化接口返回数据出现错误,传入该方法JSON串为：" + jsonStr);
				return pointsArr;
			}
			JSONObject pointsObj = resultObj.getJSONObject("points");
			for (String key : pointsObj.keySet()) {
				long time = Long.parseLong(key);
				JSONObject dataObj = new JSONObject();
				dataObj.put("time", time);
				BigDecimal bg = new BigDecimal(pointsObj.getDouble(key));
				double value = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				dataObj.put("value", value);
				pointsArr.add(dataObj);
			}

			for (int i = 0; i < pointsArr.size(); i++) {
				for (int k = 0; k < pointsArr.size() - i - 1; k++) {
					if (pointsArr.getJSONObject(k).getLong("time") > pointsArr.getJSONObject(k + 1).getLong("time")) {
						JSONObject tempObj = pointsArr.getJSONObject(k);
						pointsArr.set(k, pointsArr.getJSONObject(k + 1));
						pointsArr.set(k + 1, tempObj);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pointsArr;
	}

	/**
	 * 
	 * @Description: TODO(保留小数点后几位数)
	 * @author HYW
	 * @date 2017年10月16日 下午3:09:01
	 * @param number：要处理的数
	 *            decimals：保留的位数
	 * @return
	 */
	public static double retainDecimals(double number, int decimals) {
		if (number == 0) {
			return 0;
		}
		System.err.println("需要处理的数："+ number);
		BigDecimal b = new BigDecimal(number);
		double f1 = b.setScale(decimals, BigDecimal.ROUND_HALF_UP).doubleValue();
		return f1;
	}
}
