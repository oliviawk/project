package hitec.controller;

import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * ClassName:
 * Description: music
 * author: fukl
 * data: 2017年07月19日 下午5:05
 */
@Controller
@RequestMapping("/gjt_1")
public class FZJCController{
    private static final Logger logger = LoggerFactory.getLogger(FZJCController.class);

    
    @RequestMapping("/")
    public String index() {
         System.out.println("进入MICAPS4.html");
        return "index2";
    }
    @RequestMapping("/123")
    public String index1() {
         System.out.println("进入MICAPS4.html");
        return "gjpz";
    }
    @RequestMapping(value="/Ifo1" , method=RequestMethod.POST)
    @ResponseBody
    public List index2(@RequestBody String deals){
    	System.out.println(deals);
		GetIfo gi=new GetIfo();
		ArrayList b=(ArrayList) gi.getifo();
		ArrayList c=new ArrayList();
    	if(deals.equals("1")){
			for(int i=0;i<b.size();i++){
				Gj_table g= (Gj_table) b.get(i);
				if(g.getStats().equals("待处理")){
					c.add(g);
				}
			}
		}else if(deals.equals("2")){
			for(int i=0;i<b.size();i++){
				Gj_table g= (Gj_table) b.get(i);
				if(g.getStats().equals("已处理")){
					c.add(g);
				}
			}
		}else if(deals.equals("3")) {
			for (int i = 0; i < b.size(); i++) {
				Gj_table g = (Gj_table) b.get(i);
				if (g.getStats().equals("处理中")) {
					c.add(g);
				}
			}
		}else if(deals.equals("4")) {
			Collections.sort(b, new Comparator(){
				public int compare(Object o1, Object o2) {
					Gj_table s1=(Gj_table)o1;
					Gj_table s2=(Gj_table)o2;
					if(s1.getOcctime()>s2.getOcctime()){
						return 1;
					}else if(s1.getOcctime()==s2.getOcctime()){
						return 0;
					}else{
						return -1;
					}
				}
			});
			return b;
		}
    	//给LIST里的对象属性排序
		Collections.sort(c, new Comparator(){
			public int compare(Object o1, Object o2) {
				Gj_table s1=(Gj_table)o1;
				Gj_table s2=(Gj_table)o2;
				if(s1.getOcctime()>s2.getOcctime()){
					return 1;
				}else if(s1.getOcctime()==s2.getOcctime()){
					return 0;
				}else{
					return -1;
				}
			}
		});

    	return c;
    	
    }

    @RequestMapping(value="/Ifo2" , method=RequestMethod.POST)
    @ResponseBody
    public List index3(@RequestBody String n){
 //   	System.out.println(time);
    	//把需要的数据存在一个新的LIST里
		JSONObject k= JSONObject.fromObject(n);
		String time=k.getString("time");
		String deals=k.getString("deals");
    	ArrayList m =new ArrayList();
    	//定义当前时间戳
    	long t=System.currentTimeMillis();
    	System.out.println(t);
    	if(time.equals("72")){
    		GetIfo gi=new GetIfo();
    		ArrayList b=(ArrayList) gi.getifo();
    		for(int i =0;i<b.size();i++){
    			Gj_table s = (Gj_table) b.get(i);
    			long t1=s.getOcctime();
//    			System.out.println(t1);
    			if((t-t1*1000)<(3600000*24*3)){
    				m.add(s);
    				System.out.println("aa");
    			}
    		}
    	}else if(time.equals("24")){
    		GetIfo gi=new GetIfo();
    		ArrayList b=(ArrayList) gi.getifo();
    		for(int i =0;i<b.size();i++){
    			Gj_table s = (Gj_table) b.get(i);
    			long t1=s.getOcctime();
//    			System.out.println(t1);
    			if((t-t1*1000)<(3600000*24)){
    				m.add(s);
    				System.out.println("aa");
    			}
    		}
    	}else if(time.equals("10")){
    		GetIfo gi=new GetIfo();
    		ArrayList b=(ArrayList) gi.getifo();
    		for(int i =0;i<b.size();i++){
    			Gj_table s = (Gj_table) b.get(i);
    			long t1=s.getOcctime();
//    			System.out.println(t1);
    			if((t-t1*1000)<(3600000*10)){
    				m.add(s);
    				System.out.println("aa");
    			}
    		}
    	}else if(time.equals("1")){
    		GetIfo gi=new GetIfo();
    		ArrayList b=(ArrayList) gi.getifo();
    		for(int i =0;i<b.size();i++){
    			Gj_table s = (Gj_table) b.get(i);
    			long t1=s.getOcctime();
//    			System.out.println(t1);
    			if((t-t1*1000)<(3600000)){
    				m.add(s);
    				System.out.println("aa");
    			}
    		}
    	}else if(time.equals("0")){
    		GetIfo gi=new GetIfo();
        	ArrayList b=(ArrayList) gi.getifo();
        	m=b;
    	}


		ArrayList c=new ArrayList();
		if(deals.equals("1")){
			for(int i=0;i<m.size();i++){
				Gj_table g= (Gj_table) m.get(i);
				if(g.getStats().equals("待处理")){
					c.add(g);
				}
			}
		}else if(deals.equals("2")){
			for(int i=0;i<m.size();i++){
				Gj_table g= (Gj_table) m.get(i);
				if(g.getStats().equals("已处理")){
					c.add(g);
				}
			}
		}else if(deals.equals("3")) {
			for (int i = 0; i < m.size(); i++) {
				Gj_table g = (Gj_table) m.get(i);
				if (g.getStats().equals("处理中")) {
					c.add(g);
				}
			}
		}
		//给LIST里的对象属性排序
		Collections.sort(c, new Comparator(){
			public int compare(Object o1, Object o2) {
				Gj_table s1=(Gj_table)o1;
				Gj_table s2=(Gj_table)o2;
				if(s1.getOcctime()>s2.getOcctime()){
					return 1;
				}else if(s1.getOcctime()==s2.getOcctime()){
					return 0;
				}else{
					return -1;
				}
			}
		});
    	return c;
    }   
 
   
@RequestMapping(value="/Ifo3" , method=RequestMethod.POST)
@ResponseBody
public List index4(@RequestBody String p){
	JSONObject k= JSONObject.fromObject(p);
	String deals=k.getString("deals");
	String type=k.getString("e");
	GroudOrder a=new GroudOrder();
	ArrayList b=(ArrayList) a.groudOrder(type);
	ArrayList c=new ArrayList();
	if(deals.equals("1")){
		for(int i=0;i<b.size();i++){
			Gj_table g= (Gj_table) b.get(i);
			if(g.getStats().equals("待处理")){
				c.add(g);
			}
		}
	}else if(deals.equals("2")){
		for(int i=0;i<b.size();i++){
			Gj_table g= (Gj_table) b.get(i);
			if(g.getStats().equals("已处理")){
				c.add(g);
			}
		}
	}else if(deals.equals("3")) {
		for (int i = 0; i < b.size(); i++) {
			Gj_table g = (Gj_table) b.get(i);
			if (g.getStats().equals("处理中")) {
				c.add(g);
			}
		}
	}else if(deals.equals("4")) {
		return b;
	}

	return c;
   }
}
    
