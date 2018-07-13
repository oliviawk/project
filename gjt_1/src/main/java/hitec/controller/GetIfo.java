package hitec.controller;

import java.util.ArrayList;
import java.util.List;

public class GetIfo {
	public List getifo(){
		ArrayList<Object> a=new ArrayList<Object>();
		Gj_table gj=new Gj_table();
		gj.setDeal("待解决");
		gj.setDiscrib("连通异常，延迟818毫秒，超过了上次");
		gj.setFrom("核心路由器");
		gj.setGread("3");
		gj.setNumb("3");
		gj.setOcctime(1488511572);
		gj.setStats("已处理");
		gj.setType("连通性告警");
		gj.setAddress("10.20.67.176");
		a.add(gj);
		
		Gj_table gj1=new Gj_table();
		gj1.setDeal("待解决");
		gj1.setDiscrib("CPU使用率为75%，尝过了上阈值50");
		gj1.setFrom("核心路由器");
		gj1.setGread("1");
		gj1.setNumb("284");
		gj1.setOcctime(1491279872);
		gj1.setStats("已处理");
		gj1.setType("设备CPU告警");
		gj1.setAddress("10.20.67.176");
		a.add(gj1);
		
		Gj_table gj2=new Gj_table();
		gj2.setDeal("待解决");
		gj2.setDiscrib("内存使用率为100%，超过上阈值80");
		gj2.setFrom("云资源");
		gj2.setGread("3");
		gj2.setNumb("131");
		gj2.setOcctime(1493961866);
		gj2.setStats("已处理");
		gj2.setType("主机内存告警");
		gj2.setAddress("10.20.67.176");
		a.add(gj2);
		
		Gj_table gj3=new Gj_table();
		gj3.setDeal("待解决");
		gj3.setDiscrib("连通异常，延迟1051毫秒，超过了上次");
		gj3.setFrom("数据传送");
		gj3.setGread("3");
		gj3.setNumb("45");
		gj3.setOcctime(1496732056);
		gj3.setStats("待处理");
		gj3.setType("主机内存告警");
		gj3.setAddress("10.20.67.176");
		a.add(gj3);
		
		Gj_table gj4=new Gj_table();
		gj4.setDeal("待解决");
		gj4.setDiscrib("连通异常，延迟899毫秒");
		gj4.setFrom("数据共享");
		gj4.setGread("2");
		gj4.setNumb("2");
		gj4.setOcctime(1516158968);
		gj4.setStats("待处理");
		gj4.setType("连通性告警");
		gj4.setAddress("10.20.67.199");
		a.add(gj4);
		
		Gj_table gj5=new Gj_table();
		gj5.setDeal("待解决");
		gj5.setDiscrib("状态异常驱动状态为是");
		gj5.setFrom("云资源");
		gj5.setGread("1");
		gj5.setNumb("9");
		gj5.setOcctime(1502179122);
		gj5.setStats("待处理");
		gj5.setType("阈值警告");
		gj5.setAddress("10.20.65.199");
		a.add(gj5);
		
		Gj_table gj6=new Gj_table();
		gj6.setDeal("待解决");
		gj6.setDiscrib("连通异常，延迟1241毫秒，超过上次");
		gj6.setFrom("数据共享");
		gj6.setGread("1");
		gj6.setNumb("20");
		gj6.setOcctime(1516176968);
		gj6.setStats("待处理");
		gj6.setType("连通性告警");
		gj6.setAddress("10.20.67.177");
		a.add(gj6);
		
		Gj_table gj7=new Gj_table();
		gj7.setDeal("待解决");
		gj7.setDiscrib("状态异常，驱动状态为是");
		gj7.setFrom("核心路由器");
		gj7.setGread("2");
		gj7.setNumb("8");
		gj7.setOcctime(1516263368);
		gj7.setStats("待处理");
		gj7.setType("连通性告警");
		gj7.setAddress("10.20.67.177");
		a.add(gj7);
		
		Gj_table gj8=new Gj_table();
		gj8.setDeal("待解决");
		gj8.setDiscrib("CPU使用率为%92，超过了上阈值50");
		gj8.setFrom("数据传送");
		gj8.setGread("2");
		gj8.setNumb("20");
		gj8.setOcctime(1514333674);
		gj8.setStats("待处理");
		gj8.setType("设备CPU告警");
		gj8.setAddress("10.20.67.177");
		a.add(gj8);

		Gj_table gj9=new Gj_table();
		gj9.setDeal("待解决");
		gj9.setDiscrib("状态异常，驱动状态为是");
		gj9.setFrom("核心路由器");
		gj9.setGread("2");
		gj9.setNumb("8");
		gj9.setOcctime(1516263368);
		gj9.setStats("待处理");
		gj9.setType("连通性告警");
		gj9.setAddress("10.20.67.177");
		a.add(gj9);

		Gj_table gj10=new Gj_table();
		gj10.setDeal("待解决");
		gj10.setDiscrib("CPU使用率为%92，超过了上阈值50");
		gj10.setFrom("数据传送");
		gj10.setGread("2");
		gj10.setNumb("20");
		gj10.setOcctime(1514333674);
		gj10.setStats("处理中");
		gj10.setType("设备CPU告警");
		gj10.setAddress("10.20.67.177");
		a.add(gj10);

		Gj_table gj11=new Gj_table();
		gj11.setDeal("待解决");
		gj11.setDiscrib("状态异常，驱动状态为是");
		gj11.setFrom("核心路由器");
		gj11.setGread("2");
		gj11.setNumb("8");
		gj11.setOcctime(1516263368);
		gj11.setStats("待处理");
		gj11.setType("连通性告警");
		gj11.setAddress("10.20.67.177");
		a.add(gj11);

		Gj_table gj12=new Gj_table();
		gj12.setDeal("待解决");
		gj12.setDiscrib("CPU使用率为%92，超过了上阈值50");
		gj12.setFrom("数据传送");
		gj12.setGread("2");
		gj12.setNumb("20");
		gj12.setOcctime(1514333674);
		gj12.setStats("处理中");
		gj12.setType("设备CPU告警");
		gj12.setAddress("10.20.67.177");
		a.add(gj12);

		Gj_table gj13=new Gj_table();
		gj13.setDeal("待解决");
		gj13.setDiscrib("CPU使用率为%92，超过了上阈值50");
		gj13.setFrom("数据传送");
		gj13.setGread("2");
		gj13.setNumb("20");
		gj13.setOcctime(1514333674);
		gj13.setStats("处理中");
		gj13.setType("设备CPU告警");
		gj13.setAddress("10.20.67.177");
		a.add(gj13);

		Gj_table gj14=new Gj_table();
		gj14.setDeal("待解决");
		gj14.setDiscrib("CPU使用率为%92，超过了上阈值50");
		gj14.setFrom("数据传送");
		gj14.setGread("2");
		gj14.setNumb("20");
		gj14.setOcctime(1514333674);
		gj14.setStats("处理中");
		gj14.setType("设备CPU告警");
		gj14.setAddress("10.20.67.177");
		a.add(gj14);

		Gj_table gj15=new Gj_table();
		gj15.setDeal("待解决");
		gj15.setDiscrib("CPU使用率为%92，超过了上阈值50");
		gj15.setFrom("数据传送");
		gj15.setGread("2");
		gj15.setNumb("20");
		gj15.setOcctime(1514333674);
		gj15.setStats("处理中");
		gj15.setType("设备CPU告警");
		gj15.setAddress("10.20.67.177");
		a.add(gj15);

		return a;
	}
}
