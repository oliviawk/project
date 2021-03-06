package com.cn.hitec.service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FZJCSendConsumer extends MsgConsumer {
	private static final Logger logger = LoggerFactory.getLogger(FZJCSendConsumer.class);
	private static String topic = "SEND";
	private static String type = "FZJC";

	@Value("${FZJC.send.target.ips}")
	private String ips;
	@Value("${FZJC.datatype}")
	private String datatypes;
	@Value("${collect}")
	private String collect;
	@Value("${send}")
	private String send;

	public FZJCSendConsumer(@Value("${FZJC.group.id}")String group) {
		super(topic,group,type);
	}

	@Override
	public List<String> processing(String msg){

		List<String> toEsJsons = new ArrayList<>();

		Pattern ipspattern = Pattern.compile(ips);
		Matcher matcher = ipspattern.matcher(msg);
		if(!matcher.find()){
			return toEsJsons;
		}

		try{

			String[] lines = msg.split("\\※|\\?");
			String date = "";
			String beginTime = "";
			String endTime = "";
			String ip = "";
			String target_ip = "";
			String type = "";
			boolean add = false;

			Pattern ippattern = Pattern.compile("^(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})$");
			Pattern typepattern = Pattern.compile("log_tran_(.+)_(\\d{8})");
			Pattern timepattern1 = Pattern
					.compile("(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}).+(\\d{2}:\\d{2}:\\d{2})");
			Pattern timepattern2 = Pattern
					.compile("(\\d{2}:\\d{2}:\\d{2})");
			Pattern contextpattern = Pattern
					.compile("(.+) transfer completed with status (\\d+)\\. Total time:([0-9|\\.]+) sec\\. File size:(\\d+) bytes\\. File mtime:([0-9|:]+)");

			SimpleDateFormat df1 = new SimpleDateFormat("yyyyMMddHH:mm:ss");
			SimpleDateFormat df2 = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss.SSSZ");
			List<JSONObject> list = new ArrayList<JSONObject>();
			long receive_time = new Date().getTime();
			for (int i = 0; i < lines.length; i++) {
				if("".equals(ip)){
					matcher = ippattern.matcher(lines[i]);
					if (matcher.find()) {
						ip = matcher.group(1);
					}
					continue;
				}



				matcher = typepattern.matcher(lines[i]);
				if (matcher.find()) {
					type = matcher.group(1);
					date = matcher.group(2);

					if(!datatypes.contains(type)){
						return toEsJsons;
					}
				} else {
					matcher = timepattern1.matcher(lines[i]);
					if (matcher.find()) {
						target_ip = matcher.group(1);
						if(ips.contains(target_ip)){
							add = true;
							beginTime = endTime = matcher.group(2);

							Date end = df1.parse(date + endTime);

							if(list.size() > 0){
								for (JSONObject obj : list) {

									obj.getJSONObject("fields").element("end_time", df2.format(end));
									obj.put("occur_time", end.getTime());

									toEsJsons.add(obj.toString());

									System.out.println(obj.toString());
								}

								list.clear();

							}
						}
						else{
							add = false;
						}

					} else {
						matcher = contextpattern.matcher(lines[i]);
						if (matcher.find() && add) {

							JSONObject obj = new JSONObject();
							JSONObject subobj = new JSONObject();
							obj.put("receive_time", receive_time);

							subobj.put("start_time", df2.format(df1.parse(date + beginTime)));

							subobj.put("ip_addr", ip);
							subobj.put("ip_addr_target", target_ip);
							if(collect.contains(target_ip)){
								subobj.put("module", "采集");
							}
							else if(send.contains(target_ip)){
								subobj.put("module", "分发");
							}

							if(type.equals("satellite") || type.equals("cloudmap_Guowuyuan") || type.equals("cloudProduct")){
								String time = "";
								String[] arr = matcher.group(1).split("_");
								//SEVP_NSMC_WXGN_FY2G_E99_ACHN_LNO_P9_20171018060000000.png
								//SEVP_NSMC_WXGN_FY2G_E99_ACHN_LNO_P9_20170904190000000.HDF
								//FY4A-_AGRI--_N_DISK_1047E_L1-_FDI-_MULT_NOM_20180802050000_20180802051459_4000M_V0001.HDF
								//FY4A-_AGRI--_N_DISK_1047E_L1-_FDI-_MULT_NOM_4000M_V0001_20180802120000000.png
								if(matcher.group(1).startsWith("FY4A-_AGRI--_N_DISK_1047E_L1-_FDI-_MULT_NOM_4000M_V0001_")){
									obj.put("type", "FY4A");
									obj.put("name", "FY4A");
									time = arr[11].substring(0,arr[11].indexOf("."));
								}
								else if(matcher.group(1).startsWith("FY4A-_AGRI--_N_DISK_1047E_L1-_FDI-_MULT_NOM_")){
									obj.put("type", "FY4A");
									obj.put("name", "FY4A");
									time = arr[9] + "000";
								}
								else if(matcher.group(1).startsWith("SEVP_NSMC_WXGN_FY2G_E99_ACHN_LNO_P9_")){
									obj.put("type", "云图");
									obj.put("name", arr[3]);
									time = arr[8].substring(0,arr[8].indexOf("."));
								}
								else {
									continue;
								}

								SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");
								Date d = df.parse(time);
								if(matcher.group(1).endsWith(".HDF")){
									d.setHours(d.getHours()+8);
								}
								df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
								subobj.put("data_time", df.format(d));
							}
							else if(type.equals("radarlatlon")){
								//ACHN.QREF000.20170927.160600.latlon

								if(send.contains(target_ip)){
									continue;
								}

								String[] arr = matcher.group(1).split("\\.");
								obj.put("type", "雷达");
								obj.put("name", arr[0] + "." + arr[1]);
								String time = arr[2] + arr[3];

								SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
								Date d = df.parse(time);
								d.setHours(d.getHours()+8);
								df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
								subobj.put("data_time", df.format(d));
							}
							else if(type.equals("radar_Guowuyuan") || type.equals("radarProduct")){
								//MSP3_PMSC_RADAR_BREF_L88_CHN_201710170736_00000-00000.PNG
								String[] arr = matcher.group(1).split("_");
								obj.put("type", "雷达");
								obj.put("name", "雷达");
								String time = arr[6];

								SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
								Date d = df.parse(time);
								df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
								subobj.put("data_time", df.format(d));
							}
							else if(type.equals("T639_Guowuyuan") || type.equals("T639product")){
								//T639_GMFS_WIND_2017101613.json
								String[] arr = matcher.group(1).split("_");
								obj.put("type", "T639");
								obj.put("name", "T639");
								String time = arr[3].substring(0,arr[3].indexOf("."));

								SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHH");
								Date d = df.parse(time);
								df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
								subobj.put("data_time", df.format(d));
							}
							else if(type.equals("ec_wind") || type.equals("")){
								//EC_GLOBAL_WIND_2018080200.json
								String[] arr = matcher.group(1).split("_");
								obj.put("type", "EC风流场");
								obj.put("name", "EC");
								String time = arr[3].substring(0,arr[3].indexOf("."));

								SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHH");
								Date d = df.parse(time);
								df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
								subobj.put("data_time", df.format(d));
							}
							else if(type.equals("hotIndex_Guowuyuan") || type.equals("hotIndex")){
								//hot2017101707.txt
								String fname = matcher.group(1);
								obj.put("type", "炎热指数");

								String time = fname.substring(3,fname.indexOf("."));

								SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHH");
								Date d = df.parse(time);
								df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
								subobj.put("data_time", df.format(d));
							}
							else if(type.equals("kongqiwuran_Guowuyuan")){
								//SEVP_NMC_APWF_SFER_EAIRP_ACHN_LNO_P9_20171017120007224.JPG
								String[] arr = matcher.group(1).split("_");
								obj.put("type", "空气污染");

								String time = arr[8].substring(0,arr[8].indexOf("."));
								obj.put("name", time.substring(13,15));
								SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHH");
								Date d = df.parse(time.substring(0,10));
								df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
								subobj.put("data_time", df.format(d));
							}
//							else if(type.equals("lapsSUN")){
//								//MSP3_PMSC_RADARIV_PRCPV_L88_CHN_201806281700_02400-00600.PNG
//							}
							else{//,LapsTemperature,dizhizaihai,forestfire,micapsJson,shanhong
								obj.put("type", type);
							}

							subobj.put("file_name", matcher.group(1));
							subobj.put("event_status", matcher.group(2));
							subobj.put("totalTime", matcher.group(3));
							subobj.put("file_size", matcher.group(4));
							subobj.put("mtime", matcher.group(5));
							if (!matcher.group(2).equals("0")) {
								i++;
								subobj.put("event_info", lines[i]);
							}

							obj.put("fields", subobj);

							list.add(obj);
						}
						else if(lines[i].contains("process end")){
							matcher = timepattern2.matcher(lines[i]);
							if(matcher.find()){
								endTime = matcher.group(1);

								Date end = df1.parse(date + endTime);

								if(list.size() > 0){
									for (JSONObject obj : list) {

										obj.getJSONObject("fields").element("end_time", df2.format(end));
										obj.put("occur_time", end.getTime());

										toEsJsons.add(obj.toString());

										System.out.println(obj.toString());
									}

									list.clear();

								}
							}
						}
					}
				}
			}
		}catch(Exception e){
			logger.error("!!!!!!error");
			logger.debug("",e);
			System.out.println(msg);
			e.printStackTrace();
		}

		return toEsJsons;
	}

}