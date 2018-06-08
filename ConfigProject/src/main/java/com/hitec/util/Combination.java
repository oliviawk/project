package com.hitec.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 
 * @description: TODO 排列组合
 * @author james
 * @since 2017年7月20日 下午1:58:05 
 * @version 
 *
 */
public class Combination {
	/**
	 * 输入字符数组 固定前几位   后的排列
	 * 如 a  b  c  
	 * @param ss
	 * @param i
	 */
	public static void permutation(char[]ss,int i,List<String> result){  
        if(ss==null||i<0 ||i>ss.length){  
            return;  
        }  
        if(i==ss.length){  
        	result.add(new String(ss));
            //System.out.println(new String(ss));  
        }else{  
            for(int j=i;j<ss.length;j++){  
                char temp=ss[j];//交换前缀,使之产生下一个前缀  
		        ss[j]=ss[i];  
		        ss[i]=temp;  
		        permutation(ss,i+1,result);  
		        temp=ss[j]; //将前缀换回来,继续做上一个的前缀排列.  
		        ss[j]=ss[i];  
		        ss[i]=temp;  
            }  
        }  
	}
	
	/**
	 * 组合
	 * @param chs
	 * 如果输入abc，它的组合有a、b、c、ab、ac、bc、abc。
	 */
	 public static void combiantion(char chs[],List<String> result){  
	        if(chs==null||chs.length==0){  
	            return ;  
	        }
	        
	        List<Character> list=new ArrayList();  
	        for(int i=1;i<=chs.length;i++){  
	            combine(chs,0,i,list,result);  
	        }
	    }
	    //从字符数组中第begin个字符开始挑选number个字符加入list中  
	    public static void combine(char []cs,int begin,int number,List<Character> list,List<String> result){  
	        if(number==0){
	        	 StringBuilder str = new StringBuilder();
	             for (Character character : list) {// 对ArrayList进行遍历，将字符放入StringBuilder中
	                 str.append(character);
	             }
	        	result.add(str.toString());
	            //System.out.println(str.toString());  
	            return ;  
	        }
	        
	        if(begin==cs.length){  
	            return;  
	        }
	        
	        list.add(cs[begin]);  
	        combine(cs,begin+1,number-1,list,result);  
	        list.remove((Character)cs[begin]);  
	        combine(cs,begin+1,number,list,result);  
	    } 
}
