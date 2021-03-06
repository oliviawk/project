package com.hitec.controller;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import com.hitec.domain.*;
import com.hitec.repository.jpa.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hitec.feign.client.UpdStrategyTimer;
import com.hitec.feign.client.UpdStrategyWrite;


/**
 * ClassName:
 * Description: music
 * author: guxinyu
 * data: 2018年2月7日 下午5:05
 */
@Controller
@RequestMapping("/pjpz")
public class PJPZController {
	private static final Logger logger = LoggerFactory.getLogger(PJPZController.class);

	@Autowired
	SendTemplateRepository sendTemplateRepository;
	@Autowired
	UsersRepository userMessageRepository;
	@Autowired
	DataInfoRepository dataInfoRepository;
	@Autowired
	AlertStrategyRepository alertStrategyRepository;

	@Autowired
	UpdStrategyTimer updStrategyTimer;
	@Autowired
	UpdStrategyWrite updStrategyWrite;
	@Autowired
	DataSourceSettingRepository dataSourceSettingRepository;
   @Autowired
   Basesource_rulesRepository basesource_rulesRepository;
   @Autowired
   Basesource_userRepository basesource_userRepository;
   @Autowired
   UsersRepository usersRepository;
	@RequestMapping("/")
	public String index() {

		return "pjpz";
	}


	@RequestMapping(value = "/sen_list", method = RequestMethod.POST)
	@ResponseBody
	public List<SendTemplate> give_1() {
		Map<String,Object> outMap = new HashMap<String,Object>();
		List<SendTemplate> list = sendTemplateRepository.findAll();
		return list;

	}

	@RequestMapping(value = "/user_list", method = RequestMethod.POST)
	@ResponseBody
	public List<Users> give_2() {
		List<Users> list = userMessageRepository.findAllParent();
		return list;

	}



	@RequestMapping(value = "/pz_list", method = RequestMethod.POST)
	@ResponseBody
	public List<DataInfo> give_21() {
		List list=dataInfoRepository.findAll(0);
		return list;

	}

	@RequestMapping("/toadd")
	@ResponseBody
	public List addUser1(@RequestBody String add) {
		JSONObject ad=JSONObject.parseObject(add);
		SendTemplate st= new SendTemplate();
//		st.setId(l);
		st.setName(ad.getString("name"));
		st.setType(ad.getString("type"));
		st.setWechartContentTemplate(StringUtils.isEmpty(ad.getString("wechart_content")) ? "" : ad.getString("wechart_content"));
		st.setWechartSendEnable(StringUtils.isEmpty(ad.getString("wechart_send")) ? "0" : ad.getString("wechart_send"));
		st.setSmsContentTemplate(StringUtils.isEmpty(ad.getString("sns_content")) ? "" : ad.getString("sns_content"));
		st.setSmsSendEnable(StringUtils.isEmpty(ad.getString("sms_send")) ? "0" : ad.getString("sms_send"));
		sendTemplateRepository.save(st);
		List list=sendTemplateRepository.findAll();
		return list;

	}

	@RequestMapping("/usertoadd")
	@ResponseBody
	public List<Object> addUser4(@RequestBody String add){
		JSONObject ad=JSONObject.parseObject(add);
		int is=0;
		Users user=new Users();
		user.setIs_user(is);
		user.setDescs(ad.getString("usersm"));
		user.setWechart(ad.getString("usergzh"));
		user.setEmail(ad.getString("useremail"));
		user.setPhone(ad.getString("userphone"));
		user.setName(ad.getString("username"));
		user.setParent_id(ad.getInteger("usergroup"));
//		Users user=new Users(username,user_p,useremail,usergzh,userphone,usersm,is);
		userMessageRepository.save(user);
		List<Object> list = userMessageRepository.findUsers_pname("","","","");

		return list;
	}



	@RequestMapping(value = "/sen_d", method = RequestMethod.POST)
	@ResponseBody
	public SendTemplate give_2(@RequestBody String id) {
		JSONObject jb=JSON.parseObject(id);
		long l=Long.parseLong(jb.getString("id"));
		List<SendTemplate> list = sendTemplateRepository.findAll();
		for(int i=0;i<list.size();i++){
			SendTemplate st = list.get(i);
			if(l==st.getId()){
				return st;
			}

		}

		return null;
	}
	@RequestMapping(value = "/sen_c", method = RequestMethod.POST)
	@ResponseBody
	public Users give_1(@RequestBody String id) {
		JSONObject jb=JSON.parseObject(id);
		long l=Long.parseLong(jb.getString("id"));
		List<Users> list = usersRepository.findAll();
		for(int i=0;i<list.size();i++){
			Users st = list.get(i);
			if(l==st.getId()){
				return st;
			}

		}

		return null;
	}
	@RequestMapping(value = "/search", method = RequestMethod.POST)
	@ResponseBody
	public List give_5(@RequestBody String search_ifo) {
		JSONObject jb=JSON.parseObject(search_ifo);
		String tempName=jb.getString("tempName");
		String tempType=jb.getString("tempType");
		String tempWechart=jb.getString("tempWechart");
		String tempSms=jb.getString("tempSms");

		List<SendTemplate> list = sendTemplateRepository.findTemp(tempName.isEmpty()? "":tempName,tempType.isEmpty()? "":tempType,tempWechart.isEmpty()?"":tempWechart,tempSms.isEmpty()?"":tempSms);
		return list;
	}


	@RequestMapping(value = "/users_pname", method = RequestMethod.POST)
	@ResponseBody
	public List findUsers_pname(@RequestBody String search_ifo) {
		JSONObject jb=JSON.parseObject(search_ifo);
		String user_group1= jb.getString("user_group");
		String user_name=jb.getString("user_name");
		String user_phone=jb.getString("user_phone");
		String user_wechart=jb.getString("user_wechart");

		if(user_group1.equals("1")){
			user_group1="";
		}

		List<Object> list = userMessageRepository.findUsers_pname(user_group1.isEmpty()?"":user_group1,user_name.isEmpty()? "":user_name,user_phone.isEmpty()?"":user_phone,user_wechart.isEmpty()?"":user_wechart);

		return list;
	}

	@RequestMapping(value = "/pzSearch", method = RequestMethod.POST)
	@ResponseBody
	public List<DataInfo> give_71(@RequestBody String search_ifo) {
		List list= new ArrayList();
		if(StringUtils.isEmpty(search_ifo)){
			return list;
		}
		Map<String,String> map = JSON.parseObject(search_ifo,Map.class);

		long select3 = 0,select2 = 0,select1 = 0;

		if (map.containsKey("select1") && !StringUtils.isEmpty(map.get("select1"))){
			select1 = Long.parseLong(map.get("select1"));
		}

		if (map.containsKey("select2") && !StringUtils.isEmpty(map.get("select2"))){
			select2 = Long.parseLong(map.get("select2"));
		}
		if (map.containsKey("select3") && !StringUtils.isEmpty(map.get("select3"))){
			select3 = Long.parseLong(map.get("select3"));
		}
		/*
		if (map.containsKey("select4") && !StringUtils.isEmpty(map.get("select4"))){
			select4 = Long.parseLong(map.get("select4"));
		}
		*/
		if(select1 < 0){
			list = dataInfoRepository.findAll(0);
		}else if(select2 < 0 ){
			list = dataInfoRepository.findAll(select1);
		}else if(select3 < 0){
			list = dataInfoRepository.findAll(select2);
		}else{
			list = dataInfoRepository.findAll(select3);
		}
		/*
		else if(select4 > 0){
			list = new ArrayList();
			DataInfo di  = dataInfoRepository.findAllById(select4);
			list.add(di);
		}
		*/
		return list;
	}
	@RequestMapping(value = "/usertodelet", method = RequestMethod.POST)
	@ResponseBody
	public List<Object> give_3(@RequestBody String id) {
		JSONObject jb=JSON.parseObject(id);
		long l=Long.parseLong(jb.getString("id"));
		userMessageRepository.delete(l);
		List<Object> list = userMessageRepository.findUsers_pname("","","","");
		return list;
	}
	@RequestMapping(value = "/pztodelet", method = RequestMethod.POST)
	@ResponseBody
	public void pztodelet(@RequestBody String id) {
		//这种写法不对，不是删除data_info 里的数据
		JSONObject jb=JSON.parseObject(id);
//		Long l=Long.parseLong(jb.getString("id"));
//		alertStrategyRepository.deleteByDi_id(l);
		// data_info中的数据不用删，只删alert_strategy的数据 by Edward
		int pid = jb.getInteger("id");
		List<Object> list = dataInfoRepository.initSelected(pid);
		for (Object i : list) {
			Long di_id = ((BigInteger)((Object[])i)[0]).longValue();
			alertStrategyRepository.deleteByDi_id(di_id);
		}
	}
	@RequestMapping(value = "/temptodelet", method = RequestMethod.POST)
	@ResponseBody
	public List give_4(@RequestBody String id) {
		JSONObject jb=JSON.parseObject(id);
		long l=Long.parseLong(jb.getString("id"));
		sendTemplateRepository.delete(l);
		List<SendTemplate> list = sendTemplateRepository.findAll();
		return list;
	}
	@RequestMapping(value = "/basic_ifo", method = RequestMethod.POST)
	@ResponseBody
	public List give_selected(@RequestBody String json) {
		JSONObject object = JSON.parseObject(json);
		int n = object.getInteger("pid");
		List<Object> list = dataInfoRepository.initSelected(n);
		return list;
	}
	@RequestMapping(value = "/changeSelect1", method = RequestMethod.POST)
	@ResponseBody
	public List give_7(@RequestBody String op) {
		JSONObject jb=JSON.parseObject(op);
		String option=jb.getString("option");
		int opt= Integer.parseInt(option);
		List<DataInfo> list = dataInfoRepository.findAll();
		List<DataInfo> list1= new ArrayList<>();
		for(int i=0;i<list.size();i++){
			DataInfo dio=list.get(i);
			if(opt == dio.getParent_id()){
				list1.add(dio);
			}
		}

		return list1;
	}
	@RequestMapping(value = "/pzUpdate", method = RequestMethod.POST)
	@ResponseBody
	public String give_8(@RequestBody String op) {
		try {
			JSONObject jb=JSON.parseObject(op);
			String option=jb.getString("option");
			long id = 0;
			if(jb.containsKey("pznameid")){
				id=jb.getLong("pznameid");
			}else {
				return "id 为空";
			}
			int level=jb.getInteger("alertLevel");
			String timeout=jb.getString("pzAddtimeyz");

			//添加策略表		回头还要改为添加多个
			AlertStrategy as=new AlertStrategy("",jb.getString("userId"),jb.getString("weChartContent"),jb.getInteger("weChart"),jb.getString("smsContent"),jb.getInteger("sms"),id,jb.getInteger("selectTemp"));
			alertStrategyRepository.save(as);

			Long[] longs = null;
			DataInfo di = dataInfoRepository.findAllById(id);
			if(di == null || di.getIs_data() != 1){
				List<DataInfo> dis =dataInfoRepository.findAll(id);
				longs = new Long[dis.size()];
				for (int i = 0 ; i < dis.size() ; i++){
					longs[i] = dis.get(i).getId();
				}
			}else{
				longs = new Long[1];
				longs[0] = di.getId();
			}
			dataInfoRepository.updateWhereIds(level,timeout,longs);
			//往alert表里存
			//删除原有的绑定的策略
			//....
		} catch (Exception e) {
			e.printStackTrace();
			return "异常数据";
		}


		return "添加成功";
	}
	@RequestMapping(value = "/changeContent", method = RequestMethod.POST)
	@ResponseBody
	public SendTemplate give_9(@RequestBody String d) {
		JSONObject qi = JSON.parseObject(d);
		int id = qi.getInteger("id");
		List list=sendTemplateRepository.findAll();
		SendTemplate stp=new SendTemplate();
		for(int i=0;i<list.size();i++){
			SendTemplate st= (SendTemplate) list.get(i);
			if(st.getId()==id){
				stp=st;
			}
		}

		return stp;
	}

	@RequestMapping(value = "/findstrategy", method = RequestMethod.POST)
	@ResponseBody
	public AlertStrategy findAlertStrategy(@RequestBody String d) {
		JSONObject qi = JSON.parseObject(d);
		long data_id = qi.getLong("data_id");
		AlertStrategy alertStrategy= alertStrategyRepository.findOne(data_id);
		return alertStrategy;
	}

	@RequestMapping("/toupdate")
	public String addUser3(HttpServletRequest request, ModelMap map) {
		String id=request.getParameter("id");
		int iD=Integer.parseInt(id);
		String name=request.getParameter("name");
		String type=request.getParameter("type");
		String wechart_content=request.getParameter("wechart_content");
		String wei_send=request.getParameter("wei_send");
		String sns_content=request.getParameter("sns_content");
		String sms_send=request.getParameter("sms_send");
		sendTemplateRepository.updateWhereId(iD,name,type,wechart_content,wei_send,sns_content,sms_send);
		alertStrategyRepository.updateDataTemplate(iD,wechart_content,sns_content);
		return "pjpz";
	}
	@RequestMapping("/toupdateu")
	public String addUser7(HttpServletRequest request, ModelMap map) {
		String id=request.getParameter("id");
		long iD=Long.parseLong(id);
		String name=request.getParameter("name");
		String wechat=request.getParameter("wechat");
		String phone=request.getParameter("phone");
		String email=request.getParameter("email");
		String descs=request.getParameter("descs");
		System.out.println(name+wechat+phone+email+descs+iD);
		usersRepository.updata_u(name,wechat,phone,email,descs,iD);

		return "pjpz";
	}


	/**
	 * 查询策略信息
	 * @return
	 */
	@RequestMapping(value = "/look_strategy", method = RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> loog_strategy(@RequestBody String json) {
		Map<String,Object> resultMap = new HashMap<>();
		SendTemplate stp= null;
		try {
			JSONObject jsonObject = JSON.parseObject(json);

			if(!jsonObject.containsKey("strid")){
				resultMap.put("result","strId is null");
				return resultMap;
			}

			Map<String, Object> dataMap = new HashMap<>();

			int pid = jsonObject.getInteger("strid");
			List<Object> list = dataInfoRepository.initSelected(pid);
			dataMap.put("config", list);

			List<Object> list2 = alertStrategyRepository.findDesc(pid);
			// 再查每个环节下的告警配置 by Edward
//			for (Object i : list) {
//				Long lid = ((BigInteger)((Object[])i)[0]).longValue();
//				List<Object> l = alertStrategyRepository.findDesc(lid);
//				list2.add(l.get(0));
//			}
			dataMap.put("alert", list2);

			resultMap.put("result","ok");
			resultMap.put("data", dataMap);
		} catch (Exception e) {
			resultMap.put("result","error:"+e);
			e.printStackTrace();
		}

		return resultMap;
	}


	@RequestMapping(value = "/addstrategy", method = RequestMethod.POST)
	@ResponseBody
	public String addStrategy(@RequestBody String op) {
		try {

			JSONObject jb=JSON.parseObject(op);
			JSONObject strategyObj = jb.getJSONObject("strategy");
			JSONArray datainfoArray = jb.getJSONArray("datainfo");
			List<DataInfo> list= new ArrayList<DataInfo>();
			DataInfo dataInfo = null;
			for (int i = 0; i < datainfoArray.size(); i++){
				JSONObject diObj = datainfoArray.getJSONObject(i);
				long id = diObj.getLong("id");
				int regular = diObj.getInteger("regular");
				String timeoutValue = diObj.getString("timeoutValue");
				String shouldtimeValue = diObj.getString("shouldtimeValue");
				String monitorTimes = diObj.getString("monitorTimes");
				String fileSizeDefine = diObj.getString("fileSizeDefine");
				String fileNameDefine = diObj.getString("fileNameDefine");
				
                DataInfo dataInfoprepare=dataInfoRepository.findqueryalldata(id);
                String  name= dataInfoprepare.getName();
                list.add(dataInfoprepare);
				DataSourceSetting dataSourceSetting=null;
				System.out.println("名字："+name);
				logger.info("业务名字"+strategyObj.getString("businesstypes"));
				if("数据源".equals(strategyObj.getString("businesstypes"))){
					logger.info("执行数据源业务！！！");
					dataSourceSetting=dataSourceSettingRepository.queryDataSourceSettingConfig(name);
					if(dataSourceSetting==null){
						logger.info("表数据不一致！！");
						throw  new  RuntimeException("表数据不一致！！");
					}
					logger.info("文件名:"+fileNameDefine+"cron表达式："+monitorTimes);
					dataSourceSetting=handletimeformat(dataSourceSetting,fileNameDefine,monitorTimes);
					if (dataSourceSetting==null){
						logger.info("日期格式不正确！！");
						throw  new  RuntimeException("日期格式不正确！！");
					}
					dataSourceSettingRepository.save(dataSourceSetting);
				}

				dataInfoRepository.updateWhereId(id,regular,timeoutValue,shouldtimeValue,monitorTimes,fileSizeDefine,fileNameDefine);
				Integer beforeAlert = diObj.getInteger("beforeAlert");
				Integer delayAlert = diObj.getInteger("delayAlert");
				String alertTimeRange = diObj.getString("alertTimeRange");
				Integer maxAlerts = diObj.getInteger("maxAlerts");
				dataInfoRepository.updateAlertRules(beforeAlert,delayAlert,alertTimeRange,maxAlerts,id);
				try {
					//删除原有的绑定的策略
					alertStrategyRepository.deleteByDi_id(id);
				} catch (Exception e) {
					logger.error(e.getMessage());
				}
				//添加策略表		回头还要改为添加多个
				AlertStrategy as=new AlertStrategy("",strategyObj.getString("userId"),strategyObj.getString("weChartContent"),strategyObj.getInteger("weChart"),strategyObj.getString("smsContent"),strategyObj.getInteger("sms"),id,strategyObj.getInteger("selectTemp"));
//				System.out.println(strategyObj.getInteger("selectTemp"));
				alertStrategyRepository.save(as);

				if (dataInfo == null){
					dataInfo = dataInfoRepository.findAllById(id);
				}

			}


			JSONObject j = new JSONObject();
			j.put("serviceType",dataInfo.getService_type());
			String datainfostr=JSON.toJSONString(list);

			Thread updaThread = new Thread(){
				@Override
				public void run(){
					long time = System.currentTimeMillis();
					logger.info("开启线程更新告警策略，线程ID："+this.getName());
					//更新策略
					try {
						updStrategyWrite.DeletePrepareData(datainfostr);
						updStrategyTimer.updInitMap(JSON.toJSONString(j));
						updStrategyWrite.updInitMap();
						logger.info("更新告警策略-成功，线程ID："+this.getName());
					} catch (Exception e) {
						SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm");
						logger.error("更新告警策略-失败！ 操作时间："+ sd.format(new Date(time)) +"，线程ID："+this.getName());
						StringWriter sw = new StringWriter();
						PrintWriter pw = new PrintWriter(sw);
						e.printStackTrace(pw);
						String strError = sw.toString();
						logger.error(strError.length() > 1000 ? strError.substring(0,999):strError);
					}
				}
			};
			updaThread.setName("update_alert_strategy_"+((int)(Math.random()*1000)));
			updaThread.start();

		} catch (Exception e) {
			e.printStackTrace();
			return "异常数据,或文件时间格式不正确！！！";
		}


		return "添加成功";
	}

	@RequestMapping(value="/addBaseSourceConfiger")
	@ResponseBody
	public String addBaseSourceConfiger(@RequestBody String op){
		JSONObject jb=JSON.parseObject(op);
		JSONObject strategyObj = jb.getJSONObject("strategy");
		JSONObject datainfoObj = jb.getJSONObject("datainfo");
		String ip = datainfoObj.getString("ip");
		JSONArray datainfoValue = datainfoObj.getJSONArray("value");
		String id = dataInfoRepository.ensureIpExit(ip);
		if(id!=null){
			dataInfoRepository.deleteExitIpBaseSource(id);
			alertStrategyRepository.deleteExitIpBaseSourceAlert(id+"00_");
		}else{
			List list = dataInfoRepository.findIdMaxValue();
			Collections.sort(list);
			String s = list.get(list.size()-1).toString();
			BigInteger bi = new BigInteger(s);
			long l = bi.longValue()+1;
			DataInfo di = new DataInfo(l, 1, ip, 0, 0, null, 0, null, 0, null, null,null,null, null, null, 0);
			dataInfoRepository.save(di);
			id = l+"";
		}
		if(datainfoValue.size()!=0){
			for (Object obj : datainfoValue) {
				String temp = "-1";
				String name = null;
				if(obj.toString().equals("cpu")){
					temp = "1";
					name = "CPU";
				}
				if(obj.toString().equals("mem")){
					temp = "2";
					name= "内存";
				}
				if(obj.toString().equals("disk")){
					temp = "3";
					name = "磁盘";
				}
				if(obj.toString().equals("net")) {
					temp = "4";
					name = "网络";
				}
				Long tempId = Long.parseLong(id+"00"+temp);
				DataInfo dataInfo = new DataInfo(tempId, Long.parseLong(id), name, 1, 80, null, 0, name, 0, null,null,null, null, "-", ip, 0);
//				dataInfoRepository.insertExistIpBaseSource(Long.parseLong(id+"00"+temp), Long.parseLong(id), name, 1, 80, null, 0, null, 0, null, null, "-", ip, 0);
				AlertStrategy as=new AlertStrategy(name,strategyObj.getString("userId"),strategyObj.getString("weChartContent"),strategyObj.getInteger("weChart"),strategyObj.getString("smsContent"),strategyObj.getInteger("sms"),tempId,strategyObj.getInteger("selectTemp"));
				 alertStrategyRepository.save(as);
				 dataInfoRepository.save(dataInfo);
			}
		}

		System.out.println(id);
		return null;
	}
  public DataSourceSetting handletimeformat(DataSourceSetting dataSourceSetting,String filenameone,String monitor) throws Exception{

	  String filename=filenameone;
	  String regxt="\\{[a-zA-Z]+\\}";
	  Pattern pat=Pattern.compile(regxt);
	  Matcher matcher=pat.matcher(filename);
	  String format=null;
	  if (matcher.find()){
           format=matcher.group();
           format=format.replace("{","");
           format=format.replace("}","");
           logger.info("format:"+format);
		  String regx="\\d{"+(format.length()-2)+"}";
		  String filenametwo=filename.replace("{"+format+"}",regx);
		  dataSourceSetting.setFileName(filenametwo);
		  dataSourceSetting.setTimeFormat(format);
		  dataSourceSetting.setMoniterTimer(monitor);
		  return  dataSourceSetting;
	  }
	  else {
	  	logger.info("else");
		  dataSourceSetting.setFileName(filename);
		  dataSourceSetting.setTimeFormat(format);
		  dataSourceSetting.setMoniterTimer(monitor);
		  return  dataSourceSetting;
	  }

  }
	@RequestMapping(value = "/SelectUserPhone", method = RequestMethod.POST)
	@ResponseBody
	public  Object  SelectUserPhone(HttpServletRequest request){
           String  username =request.getParameter("user");
           logger.info("用户名："+username);
           if (username.isEmpty()){
           	return null;
		   }
		   else {
            List<Users> list= userMessageRepository.SelectUserPhone(username);
            if (list.size()==1){
				String userphone=list.get(0).getPhone();

				return userphone;
			}
			else {
            	logger.info("集合长度："+list.size());
            	return null;
			}
		   }

	}

	@RequestMapping(value = "/SelectUserPhonezero", method = RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> SelectUserPhonezero() {
		Map<String,Object> map=new HashMap<String,Object>();
		List<Object> list = basesource_userRepository.queryallrules();
		List<Object> listtwo=basesource_rulesRepository.queryrules();
        map.put("listone",list);
        map.put("listtwo",listtwo);
		return map;

	}


	@RequestMapping(value = "/SaveBasicreSources", method = RequestMethod.POST)
	@ResponseBody
	@Transactional(rollbackFor = {RuntimeException.class})
	public Object SaveBasicreSources(@RequestBody String js){
		System.out.print(js);
		JSONObject json=JSON.parseObject(js);
		Integer alertnum=null;
		String alerttime=null;
		Integer alertme=null;
		Long rulesid=null;
		JSONArray tabledata = json.getJSONArray("tabledata");
		List<Basesourceuser> list=null;
        try {
			 alertnum=Integer.valueOf(json.getString("alertnum"));
			 alerttime=json.getString("alerttime");
			alertme=Integer.valueOf(json.getString("alertme"));
			rulesid=Long.valueOf(json.getString("rulesid"));
		   list	=Transformation(tabledata);
		}
		catch (Exception e){
        	logger.info("数据异常！！");
        	return "数据异常！！";
		}
		   basesource_rulesRepository.updaterules(rulesid,alertnum,alerttime,alertme);
		 for (Basesourceuser basesourceuser:list){
		 	logger.info("id:   "+ basesourceuser.getId()+"名字：  "+basesourceuser.getUser()+"电话：  "+basesourceuser.getSms()+"状态码： "+basesourceuser.getEnable());
		 	Basesourceuser bsone=basesource_userRepository.queryrules(basesourceuser.getId());
		 	if (bsone==null){
               basesource_userRepository.Addbasesource(basesourceuser.getId(),basesourceuser.getUser(),basesourceuser.getSms(),basesourceuser.getEnable());
			}
		 	else {
				basesource_userRepository.updaterules(basesourceuser.getId(),basesourceuser.getUser(),basesourceuser.getSms(),basesourceuser.getEnable());
			}
		 }
     return "保存成功！！";

	}
	public List<Basesourceuser> Transformation(JSONArray tabledata){
		List<Basesourceuser> list=new ArrayList<Basesourceuser>();
		for (Object object : tabledata){
			JSONArray a = JSONArray.parseArray(object.toString());
			Basesourceuser basesourceuser=new Basesourceuser();
			int i=1;
			for (Object objecttwo:a){
				if (i==1){
					Long id=Long.valueOf(objecttwo.toString());
					basesourceuser.setId(id);
				}
				if (i==2){
					String name= objecttwo.toString();
					basesourceuser.setUser(name);
				}
				if (i==3){
					String sms=objecttwo.toString();
					basesourceuser.setSms(sms);
				}
				if(i==4){
					Float tf=Float.valueOf(objecttwo.toString());
					basesourceuser.setEnable(tf);
				}
				i++;
                if (i==5){
                	i=1;
				}
			}
			list.add(basesourceuser);
		}
		return list;
	}

}


    
