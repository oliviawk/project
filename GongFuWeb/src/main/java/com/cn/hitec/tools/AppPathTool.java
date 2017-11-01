package com.cn.hitec.tools;

import java.io.File;

/**
 * 
 * @ClassName: AppPathTool 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @author james
 * @date 2017年4月26日 下午3:08:30 
 *
 */
public class AppPathTool {
	private static String srcPath=null; 
	
	/**
	 * 发布环境下  获取运行jar 所在路径
	 * 在开发环境下  获取classes 目录 
	 * @return
	 */
	public static String getSrcPath(){
		if(null != srcPath) return srcPath;
		
        String filePath = System.getProperty("java.class.path");  
        String pathSplit = System.getProperty("path.separator");       //windows下是";",linux下是":"  
        
        if(filePath.contains(pathSplit)){  
            filePath = filePath.substring(0,filePath.indexOf(pathSplit))+File.separator;
        }else if (filePath.endsWith(".jar")) {
        	//截取路径中的jar包名,可执行jar包运行的结果里包含".jar"   
            filePath = filePath.substring(0, filePath.lastIndexOf(File.separator) + 1);   
        }
        srcPath = filePath;
        return srcPath;  
    }  
	
	/**
	 * 获取data 所在路径
	 * data 开发环境下  位于 resources 下
	 * data 发布环境下  位于 jar所在目录下
	 * @return
	 */
	public static String getDataPath(){
		String srcPath = getSrcPath();
		
        return srcPath+"data"+File.separator;  
    }
	
	
}
