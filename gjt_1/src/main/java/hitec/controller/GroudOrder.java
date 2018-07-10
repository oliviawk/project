package hitec.controller;

import java.util.*;

public class GroudOrder {
	public List groudOrder(String type){
		 Map<String, List<Gj_table>> map =null;
		 ArrayList<Gj_table> a=new ArrayList<Gj_table>();
			
	        GetIfo haha=new GetIfo();
	        List<Gj_table> list= haha.getifo();
        
	        map = OrderSortByGroup(list,type);
//	        System.out.println(map.toString());
	        for(Map.Entry<String, List<Gj_table>> entry : map.entrySet()){        	
	            for(Gj_table o : entry.getValue()){ 
	            	if(type.equals("2")){
	            		o.setGread(entry.getKey());
	            		a.add(o);
	            	}else if(type.equals("1")){
	            		o.setType(entry.getKey());
		            	a.add(o);
	            	}else if(type.equals("3")){
	            		o.setFrom(entry.getKey());
		            	a.add(o);
	            	}else if(type.equals("4")){
	            		o.setStats(entry.getKey());
		            	a.add(o);
	            	}else if(type.equals("5")){
	            		o.setAddress(entry.getKey());
		            	a.add(o);
	            	}
//	                System.out.println(entry.getKey() +"--"+o.gread+"--"+o.occtime);
	            }
	        }
	        
		return a;
		
	}

public static Map<String, List<Gj_table>> OrderSortByGroup(List<Gj_table> list,String type){
	if(type.equals("2")){
		
    Map<String, List<Gj_table>> map = new HashMap<String, List<Gj_table>>();
    for(Gj_table order : list) {
        List<Gj_table> staList = map.get(order.gread);
        if(staList==null){
            staList = new ArrayList<Gj_table>();
        }
        Gj_table od = new Gj_table();
        od.setAddress(order.address);
        od.setDeal(order.deal);
        od.setDiscrib(order.discrib);
        od.setFrom(order.from);
        od.setNumb(order.numb);
        od.setOcctime(order.getOcctime());
        od.setStats(order.stats);
        od.setType(order.type);
        staList.add(od);
        Collections.sort(staList, new Comparator<Gj_table>() {
            public int compare(Gj_table o1, Gj_table o2) {
                return o1.getAddress().compareTo(o2.getAddress());
            }
        });
        
        map.put(order.gread, staList);
    }
    return map;
}else if(type.equals("1")){

    Map<String, List<Gj_table>> map = new HashMap<String, List<Gj_table>>();
    for(Gj_table order : list) {
        List<Gj_table> staList = map.get(order.type);
        if(staList==null){
            staList = new ArrayList<Gj_table>();
        }
        Gj_table od = new Gj_table();
        od.setAddress(order.address);
        od.setDeal(order.deal);
        od.setDiscrib(order.discrib);
        od.setFrom(order.from);
        od.setNumb(order.numb);
        od.setOcctime(order.getOcctime());
        od.setStats(order.stats);
        od.setGread(order.gread);
//        System.out.println(order.gread);
        staList.add(od);
        Collections.sort(staList, new Comparator<Gj_table>() {
            public int compare(Gj_table o1, Gj_table o2) {
                return o1.getAddress().compareTo(o2.getAddress());
            }
        });
        
        map.put(order.type, staList);
    }
    return map;

}else if(type.equals("3")){

    Map<String, List<Gj_table>> map = new HashMap<String, List<Gj_table>>();
    for(Gj_table order : list) {
        List<Gj_table> staList = map.get(order.from);
        if(staList==null){
            staList = new ArrayList<Gj_table>();
        }
        Gj_table od = new Gj_table();
        od.setAddress(order.address);
        od.setDeal(order.deal);
        od.setDiscrib(order.discrib);
        od.setType(order.type);
        od.setNumb(order.numb);
        od.setOcctime(order.getOcctime());
        od.setStats(order.stats);
        od.setGread(order.gread);
//        System.out.println(order.gread);
        staList.add(od);
        Collections.sort(staList, new Comparator<Gj_table>() {
            public int compare(Gj_table o1, Gj_table o2) {
                return o1.getAddress().compareTo(o2.getAddress());
            }
        });
        
        map.put(order.from, staList);
    }
    return map;

}else if(type.equals("4")){

    Map<String, List<Gj_table>> map = new HashMap<String, List<Gj_table>>();
    for(Gj_table order : list) {
        List<Gj_table> staList = map.get(order.stats);
        if(staList==null){
            staList = new ArrayList<Gj_table>();
        }
        Gj_table od = new Gj_table();
        od.setAddress(order.address);
        od.setDeal(order.deal);
        od.setDiscrib(order.discrib);
        od.setType(order.type);
        od.setNumb(order.numb);
        od.setOcctime(order.getOcctime());
        od.setFrom(order.from);
        od.setGread(order.gread);
//        System.out.println(order.gread);
        staList.add(od);
        Collections.sort(staList, new Comparator<Gj_table>() {
            public int compare(Gj_table o1, Gj_table o2) {
                return o1.getAddress().compareTo(o2.getAddress());
            }
        });
        
        map.put(order.stats, staList);
    }
    return map;

}else if(type.equals("5")){


    Map<String, List<Gj_table>> map = new HashMap<String, List<Gj_table>>();
    for(Gj_table order : list) {
        List<Gj_table> staList = map.get(order.address);
        if(staList==null){
            staList = new ArrayList<Gj_table>();
        }
        Gj_table od = new Gj_table();
        od.setStats(order.stats);
        od.setDeal(order.deal);
        od.setDiscrib(order.discrib);
        od.setType(order.type);
        od.setNumb(order.numb);
        od.setOcctime(order.getOcctime());
        od.setFrom(order.from);
        od.setGread(order.gread);
//        System.out.println(order.gread);
        staList.add(od);
        Collections.sort(staList, new Comparator<Gj_table>() {
            public int compare(Gj_table o1, Gj_table o2) {
                return o1.getDiscrib().compareTo(o2.getDiscrib());
            }
        });
        
        map.put(order.address, staList);
    }
    return map;


}
	return null;
	
	
}
}