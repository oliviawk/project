package com.cn.hitec.service;

import com.alibaba.fastjson.JSON;
import com.cn.hitec.bean.SearchBean;
import com.cn.hitec.bean.UpdatebyIdBean;
import com.cn.hitec.feign.client.EsService;
import net.sf.json.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.*;


@Service
public class LAPSCollectConsumerEX {


	@Autowired
	EsService esService;
	private static final Logger logger = LoggerFactory.getLogger(LAPSCollectConsumerEX.class);

	//private KafkaConsumer<String, String> consumer;

	List<String> list = new ArrayList<>();
	String topic = "LAPSEX";
	String type = "LAPS";


/*
	static int flagtem = 0;
	static 	int flagdem = 0;
	static 	int flaghumidity = 0;
	static 	int flagpre = 0;
	static 	int flagws = 0;
	static 	int flagwd = 0;
	static String fileneame = "";


	static 	List<String> listcor = new ArrayList<>();
	static 	List<String> listdev = new ArrayList<>();
	static 	List<String> listvar = new ArrayList<>();
	static 	List<String> listsamp = new ArrayList<>();

	static 	String[][] toptem = new String[21][4];
	static 	String[][] topdem = new String[21][4];
	static 	String[][] tophumidity = new String[21][4];
	static 	String[][] toppre = new String[21][4];
	static 	String[][] topws = new String[21][4];
	static 	String[][] topwd = new String[21][4];
*/


	// 设置brokerServer(kafka)ip地址


	//消费函数~

//	public  void consume(){
//		String xjf = esService.getESHealth();
//		System.out.println(xjf+"8888888889999999999999777777755555555555");
//}
	public  void consume( )
	{
		Properties props = new Properties();
		KafkaConsumer<String, String> consumer;
		props.put("bootstrap.servers", "10.30.17.173:9092,10.30.17.174:9092,10.30.17.175:9092");

		props.put("group.id", "0");

		props.put("enable.auto.commit", "false");

		// 设置使用最开始的offset偏移量为该group.id的最早。如果不设置，则会是latest即该topic最新一个消息的offset
		// 如果采用latest，消费者只能得道其启动后，生产者生产的消息
		props.put("auto.offset.reset", "latest");
		//props.put("auto.offset.reset", "earliest");
		props.put("max.partition.fetch.bytes", "248576");
		props.put("max.poll.records", "50");

		//
		props.put("session.timeout.ms", "30000");
		//	props.put("serializer.encoding", "iso-8859-1");
		props.put("key.deserializer",
				"org.apache.kafka.common.serialization.StringDeserializer");
		props.put("value.deserializer",
				"org.apache.kafka.common.serialization.StringDeserializer");
		consumer = new KafkaConsumer<String, String>(props);


	//	System.out.println(topic);
		consumer.subscribe(Arrays.asList(topic));
		long startTime = System.currentTimeMillis();
		long useaTime = 0;
		int flag = 0;
		while (true)
		{
			try
			{
				ConsumerRecords<String, String> records = consumer.poll(100);
				for (ConsumerRecord<String, String> record : records)
				{

					String msg = record.value();
					System.out.println(msg);
					process(msg);

					//flag = 1;
					//if(flag == 1)
//					if(msg.contains("温度数据"))
//					{
//						System.out.println("温度数据，正常退出");
//						consumer.commitSync();
//						//System.exit(0);
//						flag++;
//						System.out.println("wwwwwwwwwwwwwwwwwwww成功执行的次数："+flag);
//						Thread.sleep(1000);
//					}

					//	logger.error(msg);
				}
				consumer.commitSync();
			}
			catch (Exception e)
			{
				logger.error("!!!!!!error");
				logger.debug("", e);
				e.printStackTrace();
			}
		}
	}
	//处理函数~ --返回数组》》
	public  String[][]  returnarray(int flag, String fileline, String[][] top1, int sign)
	{
		String[][] top = top1;
		if(flag == 1)
		{
			String[] xjf = fileline.split("\\s+");//多个空格的划分
			if(xjf.length >= 3)
			{
				float tepvar = (Float.parseFloat(xjf[2 + sign]) - Float.parseFloat(xjf[1 + sign])) * (Float.parseFloat(xjf[2 + sign]) - Float.parseFloat(xjf[1 + sign]));
				float tep = (Float.parseFloat(xjf[2 + sign]) - Float.parseFloat(xjf[1 + sign]));
				top[20][3] = tepvar + "";
				top[20][4] = tep + "";
				//top[20][3] = tep + "";
				top[20][2] = xjf[2 + sign];
				top[20][1] = xjf[1 + sign];
				top[20][0] = xjf[0];
				//	System.out.println(tempString+"jjjjj"+i);
				for(int i1 = 20; i1 > 0; i1--)
				{
					if(Float.parseFloat(top[i1][3]) > Float.parseFloat(top[i1 - 1][3]))
					{
						String temp = top[i1 - 1][3];
						top[i1 - 1][3] = top[i1][3];
						top[i1][3] = temp;

						temp = top[i1 - 1][2];
						top[i1 - 1][2] = top[i1][2];
						top[i1][2] = temp;

						temp = top[i1 - 1][1];
						top[i1 - 1][1] = top[i1][1];
						top[i1][1] = temp;

						temp = top[i1 - 1][0];
						top[i1 - 1][0] = top[i1][0];
						top[i1][0] = temp;

						temp = top[i1 - 1][4];
						top[i1 - 1][4] = top[i1][4];
						top[i1][4] = temp;
					}
				}
			}//结束if
		}

		return top;
	}
	//处理函数~ --返回数组《《

	//处理函数~--返回字符串》》
	public  String   returnString(String type,  String[][] top1)
	{
		String[][] top = top1;

		String  temp = "";
		if(top[0][0] != "0" && top[0][0] != null)
		{
			temp = type + "站点  实况数据   融合数据   差值 \n";
		}
		for(int j = 0; j < 20; j++)
		{
			if(top[j][0] != "0" && top[j][0] != null)
			{
			//	temp = temp + String.format("%5s",top[j][0]) + " " + String.format("%10s",top[j][1]) + " " + String.format("%10s",top[j][2]) + " " + String.format("%10s",top[j][3]) + "\n ";
				temp = temp + String.format("%6s",top[j][0]) + " " + String.format("%9s",top[j][1].substring(0,top[j][1].indexOf(".")+2 )) + " " + String.format("%9s",top[j][2].substring(0,top[j][2].indexOf(".")+2)) + " " +String.format("%9s",top[j][4].substring(0,top[j][4].indexOf(".")+2 )) + "\n" +
						"";

			}
		}
		return temp;
	}
	//处理函数~--返回字符串《《


	public  void process(String args)
	{




		//一次读入一行，直到读入null为文件结束
		String tempString = "";
		int i = 0;
		int flagtem = 0;
		int flagdem = 0;
		int flaghumidity = 0;
		int flagpre = 0;
		int flagws = 0;
		int flagwd = 0;
		int flagwindvector = 0;
		// float tep = 0;

		List<String> listcor  = new ArrayList<>();//相关系数
		List<String> listdev  = new ArrayList<>();//离差
		List<String> listvar  = new ArrayList<>();//方差
		List<String> listsamp  = new ArrayList<>();//样本数量

		String[][] toptem = new String[21][5];//温度
		String[][] topdem = new String[21][5];//露点
		String[][] tophumidity = new String[21][5];//相对湿度数据
		String[][] toppre = new String[21][5];//1小时降水数据
		String[][] topws = new String[21][5];//风向风速数据
		String[][] topwd = new String[21][5];//
		String filename="";
		String[] arr = args.split("\\*");
		if(arr[0]==null||arr[0].length()==0){
			filename="";
		}else{
			filename=arr[0];
		}

		//while (( tempString = reader.readLine()) != null)
		for(int jjj = 0; jjj < arr.length; jjj++)
		{
			tempString = arr[jjj].trim();
			//tempString=tempString;

			i++;
			if 	(tempString.contains("温度数据"))
			{
				// System.out.println(tempString+"55555555555555555"+i);
				for(int j = 0; j < 20; j++)
				{
					for(int jj = 0; jj < 4; jj++)
					{
						toptem[j][jj] = 0 + "";
					}
				}
				flagtem = 1;
				//toptem = returnarray(flagtem, tempString, toptem, 0);
				//System.out.println("5555" + i);
			}

			//  if 	((" 露点数据").equals(tempString))
			if 	(tempString.contains("露点数据"))
			{

				for(int j = 0; j < 20; j++)
				{

					for(int jj = 0; jj < 4; jj++)
					{
						topdem[j][jj] = 0 + "";
					}
				}
				flagtem = 0;
				flagdem = 0;
				flaghumidity = 0;
				flagpre = 0;
				flagws = 0;
				flagwd = 0;
				flagdem = 1;

				//topdem = returnarray(flagdem, tempString, topdem, 0);
				//  System.out.println("5555" + i);
			}
			// if 	((" 相对湿度数据").equals(tempString))
			if 	(tempString.contains("相对湿度数据"))
			{

				for(int j = 0; j < 20; j++)
				{

					for(int jj = 0; jj < 4; jj++)
					{
						tophumidity[j][jj] = 0 + "";
					}
				}
				flagtem = 0;
				flagdem = 0;
				flaghumidity = 0;
				flagpre = 0;
				flagws = 0;
				flagwd = 0;
				flaghumidity = 1;
				//tophumidity = returnarray(flaghumidity, tempString, tophumidity, 0);
				//  System.out.println("5555" + i);
			}
			// if 	((" 1小时降水数据").equals(tempString))
			if 	(tempString.contains("1小时降水数据"))
			{

				for(int j = 0; j < 20; j++)
				{

					for(int jj = 0; jj < 4; jj++)
					{
						toppre[j][jj] = 0 + "";
					}
				}
				flagtem = 0;
				flagdem = 0;
				flaghumidity = 0;
				//flagpre = 0;
				flagws = 0;
				flagwd = 0;
				flagpre = 1;
				//toppre = returnarray(flagpre, tempString, toppre, 0);

			}

			if 	(tempString.contains("风矢量数据"))
			{
				flagtem = 0;
				flagdem = 0;
				flaghumidity = 0;
				//flagpre = 0;
				flagws = 0;
				flagwd = 0;
				flagpre = 0;
				flagwindvector=1;

			}

			if 	(tempString.contains("风向风速数据"))
			{

				for(int j = 0; j < 20; j++)
				{

					for(int jj = 0; jj < 4; jj++)
					{
						topws[j][jj] = 0 + "";
						topwd[j][jj] = 0 + "";
					}
				}
				flagtem = 0;
				flagdem = 0;
				flaghumidity = 0;
				flagpre = 0;
				flagws = 0;
				flagwd = 0;
				flagws = 1;
				flagwd = 1;
				//topws = returnarray(flagws, tempString, topws, 0);
				//topwd = returnarray(flagwd, tempString, topwd, 2);
			}


            				if(flagtem==1)
            				toptem=returnarray(flagtem,tempString,toptem,0);
							if(flagdem==1)
            				topdem=returnarray(flagdem,tempString,topdem,0);
			if(flaghumidity==1)
            				tophumidity=returnarray(flaghumidity,tempString,tophumidity,0);
			if(flagpre==1)
            				toppre=returnarray(flagpre,tempString,toppre,0);
			if(flagws==1)
            				topws=returnarray(flagws,tempString,topws,2);
			if(flagwd==1)
            				topwd=returnarray(flagwd,tempString,topwd,0);


			if(tempString.contains("温度露点和风矢量的相关系数"))
			{
				String[] xjf = tempString.split("\\s+");//多个空格的划分
				listcor.add("cortem:" + xjf[1]);
				listcor.add("cordem:" + xjf[2]);
				listcor.add("corwind:" + xjf[3]);
				// System.out.println(xjf[1]+"sss");
			}
			if(tempString.contains("风向和风速的相关系数"))
			{
				String[] xjf = tempString.split("\\s+");//多个空格的划分
				listcor.add("corws:" + xjf[1]);
				listcor.add("corwd:" + xjf[2]);
				// System.out.println(xjf[1]+"sss");
			}
			if(tempString.contains("相对湿度和1小时降水的相关系数"))
			{
				String[] xjf = tempString.trim().split("\\s+");//多个空格的划分
				listcor.add("corhumidity:" + xjf[1]);
				listcor.add("corpre:" + xjf[2]);
				// System.out.println(xjf[1]+"sss");
			}
			if(tempString.contains("温度露点和风矢量的离差"))
			{
				String[] xjf = tempString.split("\\s+");//多个空格的划分
				listdev.add("devtem:" + xjf[1]);
				listdev.add("devdem:" + xjf[2]);
				listdev.add("devwind:" + xjf[3]);
				// System.out.println(xjf[1]+"sss");
			}
			if(tempString.contains("风向和风速的离差"))
			{
				String[] xjf = tempString.split("\\s+");//多个空格的划分
				listdev.add("devws:" + xjf[1]);
				listdev.add("devwd:" + xjf[2]);
				// System.out.println(xjf[1]+"sss");
			}
			if(tempString.contains("相对湿度和1小时降水的离差"))
			{
				String[] xjf = tempString.trim().split("\\s+");//多个空格的划分
				listdev.add("devhumidity:" + xjf[1]);
				listdev.add("devpre:" + xjf[2]);
				// System.out.println(xjf[1]+"sss");
			}

			if(tempString.contains("温度露点和风矢量的均方根差"))
			{
				String[] xjf = tempString.split("\\s+");//多个空格的划分
				listvar.add("vartem:" + xjf[1]);
				listvar.add("vardem:" + xjf[2]);
				listvar.add("varwind:" + xjf[3]);
				// System.out.println(xjf[1]+"sss");
			}
			if(tempString.contains("风向和风速的均方根差"))
			{
				String[] xjf = tempString.split("\\s+");//多个空格的划分
				listvar.add("varws:" + xjf[1]);
				listvar.add("varwd:" + xjf[2]);
				// System.out.println(xjf[1]+"sss");
			}
			if(tempString.contains("相对湿度和1小时降水的均方根差"))
			{
				String[] xjf = tempString.trim().split("\\s+");//多个空格的划分
				listvar.add("varhumidity:" + xjf[1]);
				listvar.add("varpre:" + xjf[2]);
				// System.out.println(xjf[1]+"sss");
			}
			if(tempString.contains("相对湿度和1小时降水的样本数"))
			{
				String[] xjf = tempString.trim().split("\\s+");//多个空格的划分
				listsamp.add("samphumidity:" + xjf[1]);
				listsamp.add("samppre:" + xjf[2]);
				// System.out.println(xjf[1]+"sss");
			}
			if(tempString.contains("温度露点和风的样本数"))
			{
				String[] xjf = tempString.trim().split("\\s+");//多个空格的划分
				listsamp.add("samptem:" + xjf[1]);
				listsamp.add("sampdem:" + xjf[2]);
				listsamp.add("sampwind:" + xjf[3]);
				// System.out.println(xjf[1]+"sss");
			}

			//if (i > 1509)
			//	break;
		}//while结束
		//做判断~
		String json = "";
		String json1 = "";
		String json2 = "";
		String json3 = "";
		json = "方差：" + listvar.toString() + "\n 相关系数： " + listcor.toString() + " \n 离差： " + listdev.toString() + " \n 样本数量： " + listsamp.toString();

		json1 = json1 + returnString("温度", toptem);
		json1 = json1 + returnString("露点", topdem);
		json2 = json2 + returnString("相对湿度数据", tophumidity);
		json2 = json2 + returnString("1小时降水数据", toppre);
		json3= json3 + returnString("风速", topws);
		json3 = json3 + returnString("风向", topwd);
		System.out.println(json);
		if(json.length()>40&&filename.length()>10){
			//》》开始处理
			Map<String,Object> var = new HashMap<>();
			for(int x=0;x<listvar.size();x++){
				String temp=listvar.get(x);
				String[] temp1=temp.split(":");
				var.put(temp1[0], temp1[1]);
			}
			Map<String,Object> cor = new HashMap<>();
			for(int x=0;x<listcor.size();x++){
				String temp=listcor.get(x);
				String[] temp1=temp.split(":");
				cor.put(temp1[0], temp1[1]);
			}
			Map<String,Object> dev = new HashMap<>();
			for(int x=0;x<listdev.size();x++){
				String temp=listdev.get(x);
				String[] temp1=temp.split(":");
				dev.put(temp1[0], temp1[1]);
			}
			Map<String,Object> samp = new HashMap<>();
			for(int x=0;x<listsamp.size();x++){
				String temp=listsamp.get(x);
				String[] temp1=temp.split(":");
				samp.put(temp1[0], temp1[1]);
			}
			Map<String,Object> tem = new HashMap<>();
			tem.put("var",var.get("vartem"));
			tem.put("cor",cor.get("cortem"));
			tem.put("dev",dev.get("devtem"));
			tem.put("samp",samp.get("samptem"));
			Map<String,Object> dem = new HashMap<>();
			dem.put("var",var.get("vardem"));
			dem.put("cor",cor.get("cordem"));
			dem.put("dev",dev.get("devdem"));
			dem.put("samp",samp.get("sampdem"));
			Map<String,Object> wind = new HashMap<>();
			wind.put("var",var.get("varwind"));
			wind.put("cor",cor.get("corwind"));
			wind.put("dev",dev.get("devwind"));
			wind.put("samp",samp.get("sampwind"));
			Map<String,Object> ws = new HashMap<>();
			ws.put("var",var.get("varws"));
			ws.put("cor",cor.get("corws"));
			ws.put("dev",dev.get("devws"));
			ws.put("samp",samp.get("sampwind"));
			Map<String,Object> wd = new HashMap<>();
			wd.put("var",var.get("varwd"));
			wd.put("cor",cor.get("corwd"));
			wd.put("dev",dev.get("devwd"));
			wd.put("samp",samp.get("sampwind"));
			Map<String,Object> humidity = new HashMap<>();
			humidity.put("var",var.get("varhumidity"));
			humidity.put("cor",cor.get("corhumidity"));
			humidity.put("dev",dev.get("devhumidity"));
			humidity.put("samp",samp.get("samphumidity"));
			Map<String,Object> pre = new HashMap<>();
			pre.put("var",var.get("varpre"));
			pre.put("cor",cor.get("corpre"));
			pre.put("dev",dev.get("devpre"));
			pre.put("samp",samp.get("samppre"));
			//处理结束
			String xjf=" 温度"+tem.toString()+"\n 露点"+dem.toString()+"\n 风"+wind.toString()+"\n 风速"+ws.toString()+"\n 风向:"+wd.toString()+"\n 降水"+pre.toString()+"\n 湿度:"+humidity.toString();
			Weixin(arr[0]+"\n"+xjf);
			Weixin(json1);
			Weixin(json2);
			Weixin(json3);
//			System.out.println("wwwwwwwwwwwwwwwwwwwwwwwwwww微信执行"+json1);
//			System.out.println("wwwwwwwwwwwwwwwwwwwwwwwwwww微信执行"+json2);
//			System.out.println("wwwwwwwwwwwwwwwwwwwwwwwwwww微信执行"+json3);
			System.out.println("wwwwwwwwwwwwwwwwwwwwwwwwwww微信执行完毕");
          //  System.out.println("wwwwwwwwwwwwwwwwwwwwwwwwwww"+esService.getESHealth().toString());
			DoEs(filename,tem,dem,wind,ws,wd,pre,humidity);
		}

	}//结束proces函数


	//入库函数》》




	public  void DoEs(String filename,Map<String,Object> tem,Map<String,Object> dem,Map<String,Object> wind,Map<String,Object> ws,Map<String,Object> wd,Map<String,Object> pre,Map<String,Object> humidity) {
		String index=filename.trim().substring(0,6);

		index="data_20"+index;
		System.out.println("index:"+index+"filename："+filename);
		SearchBean esbean=new SearchBean();
		esbean.setType("LAPS");
		//esbean.setIndex("data_20180206");
		esbean.setIndex(index);

		String times = filename.substring(0,10);
		SimpleDateFormat sdf = new SimpleDateFormat("yyMMddhhmm");
		SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
		String data_time="";
		try {

			 data_time=sdf1.format(sdf.parse(times));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Map<String,Object> params = new HashMap<>();
		params.put("fields.data_time",data_time);
		//params.put("fields.data_time", "2018-02-06 02:00:00.000+0800");
		params.put("type","LSX&L1S");
		esbean.setParams(params);
		Map<String,Object> resultMap = new HashMap<>();
		System.out.println("查询xjf 88888888888"+esbean.getParams().toString()+"ssss"+esbean.getIndex());
		//resultMap=esService.search(esbean);
		for(int r=0;r<4;r++) {
			resultMap = esService.search(esbean);

			if (resultMap.isEmpty()) {

				try {
					Thread.sleep(20000);
					System.out.println("我要休息20s,第" + r + "次");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {

				String id = resultMap.get("_id").toString();
				//System.out.println("ssssssssssssssssssss"+id+"ssssssssssssssssss");
				resultMap.remove("_id");
				Object obj = resultMap.get("fields");
				Map<String, Object> fields = (Map<String, Object>) obj;
				//fields.put("xjf", "666");
				fields.put("tem",tem);
				fields.put("dem",dem);
				fields.put("wind",wind);
				fields.put("ws",ws);
				fields.put("wd",wd);
				fields.put("pre",pre);
				fields.put("humidity",humidity);
				resultMap.remove("fields");
				resultMap.put("fields", fields);
				UpdatebyIdBean updatebean = new UpdatebyIdBean();
				//updatebean.setIndex("data_20180206");
				updatebean.setIndex(index);

				updatebean.setType("LAPS");
				updatebean.setId(id);
				updatebean.setJson(JSON.toJSONString(resultMap));
				System.out.println("执行插入"+index+"字符串："+JSON.toJSONString(resultMap));
				esService.updatebyid(updatebean);
				System.out.println("执行插入结束");
				break;
			}
		}
//		System.out.println(resultMap.toString() );
//
//		System.out.println(resultMap.toString() );
//		String id = resultMap.get("_id").toString();
//		resultMap.remove("_id");
//		Object obj = resultMap.get("fields");
//		Map<String,Object> fields = (Map<String,Object>) obj;
//		//fields.put("xjf","5555555");
//		fields.put("tem",tem);
//		fields.put("dem",dem);
//		fields.put("wind",wind);
//		fields.put("ws",ws);
//		fields.put("wd",wd);
//		fields.put("pre",pre);
//		fields.put("humidity",humidity);
//		resultMap.remove("fields");
//		resultMap.put("fields",fields );
//		UpdatebyIdBean updatebean=new UpdatebyIdBean();
//		updatebean.setIndex("data_20180206");
//		updatebean.setType("LAPS");
//		updatebean.setId(id);
//		updatebean.setJson(JSON.toJSONString(resultMap));
//		esService.updatebyid(updatebean);

	}
	//入库函数结束《《



	//微信函数》》

	public  void Weixin(String args){
		//String url="http://10.16.41.126:9999/ws/sendwx/v2/1/2";
		String url="http://10.14.82.102:8999/message/send/1/2";
		Map<String,Object> params = new HashMap<>();
		params.put("safe","0");
		params.put("totag","");
		params.put("msgtype","text");
		params.put("touser","@all");
		params.put("agentid","1000007");
		params.put("toparty","");
		Map<String,String> alertMessage = new HashMap<>();
		alertMessage.put("content",args);
		params.put("text",alertMessage);
		JSONObject json = new JSONObject();
		json.putAll(params);
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(url);
		JSONObject response = null;
		String line,sb="";
		try {
			//StringEntity s = new StringEntity(json.toString());
			//System.out.println(json);
			StringEntity s = new StringEntity(json.toString(), "utf-8" );
			s.setContentEncoding("utf-8");
			s.setContentType("application/json");
			//System.out.println( s.toString());
			post.setEntity(s);

			HttpResponse res = client.execute(post);
			url = URLDecoder.decode(url, "UTF-8");
			if(res.getStatusLine().getStatusCode() == 200 ){
				HttpEntity entity = res.getEntity();
				String charset = EntityUtils.getContentCharSet(entity);
				//System.out.println(charset);
				BufferedReader br = new BufferedReader(new InputStreamReader(entity.getContent(),charset));
				//  String line,sb="";
				while((line = br.readLine())!=null){
					// sb.append(line);
					sb=sb+line;
				}
			}
			System.out.println(sb);
		} catch (Exception e) {
			System.out.println("微信异常"+e);
			throw new RuntimeException(e);
		}


	}



	//以行为单位读取文件end
}






