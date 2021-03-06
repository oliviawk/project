package hitec.controller;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import com.sun.org.apache.bcel.internal.generic.I2F;
import hitec.domain.User_Catalog;
import hitec.feign.client.DataSourceEsInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import hitec.domain.DataInfo;
import hitec.domain.DataSourceSetting;
import hitec.feign.client.DataSourceKafkaInterface;
import hitec.service.DataSourceSettingService;

@Controller
@RequestMapping("/dataSourceSetting")
public class DataSourceSettingController {

	@Autowired
	DataSourceSettingService dataSourceSettingService;
	@Autowired
	DataSourceKafkaInterface dataSourceKafkaInterface;
    @Autowired
	DataSourceEsInterface dataSourceEsInterface;

	@RequestMapping("/")
	public String index() {
		return "dataSource/dataSourceSetting";
	}

//	@RequestMapping("/2")
//	public String index2() {
//		return "dataSource/dataSourceSetting2";
//	}
	/**
	 * @Description 获取所有可能需要的数据的文件名称 TODO
	 * 
	 *              <pre>
	 * @author HuYiWu
	 * 
	 *         <pre>
	 * @date 2018年4月19日 下午3:44:50
	 * 
	 *       <pre>
	 */
	@RequestMapping(value = "/getPossibleNeedDataFileName", method = RequestMethod.POST)
	@ResponseBody
	public Object getPossibleNeedDataFileName() {
		return dataSourceSettingService.getPossibleNeedDataFileName();
	}

	/**
	 * @Description 插入一条元数据信息 TODO
	 * 
	 *              <pre>
	 * @author HuYiWu
	 * 
	 *         <pre>
	 * @date 2018年4月19日 下午3:45:35
	 * 
	 *       <pre>
	 */
	@RequestMapping(value = "/insertDataSource", method = RequestMethod.POST)
	@ResponseBody
	@Transactional(rollbackFor = {RuntimeException.class})
	public Object insertDataSource(HttpServletRequest request) {
		Map<String, Object> outData = new HashMap<String, Object>();
		String type = "success";
	    String message = "成功";
		String deleteId = request.getParameter("deleteId");

	try{
//		DataInfo dataInfo = dataSourceSettingService.insertDataInfo(request);// 添加datainfo为了预生成数据
//		if (dataInfo == null) {
//			outData.put("type", "fail");
//			outData.put("message", "添加数据返回为空");
//			return outData;
//		}
//		DataSourceSetting insertResult = dataSourceSettingService.insertDataSource(request);// 添加数据库
//		if (insertResult == null) {
//			outData.put("type", "fail");
//			outData.put("message", "添加元数据返回为空");
//			return outData;
//		}
//		if (insertResult.getSendUser().equals("DataSourceSetting数据重复")){
//			outData.put("type", "fail");
//			outData.put("message", "添加的元数据已存在不能重复添加！！！");
//			return outData;
//		}

		 outData=dataSourceSettingService.InsertData(request,outData);
		 Object obj=outData.get(type);
		 if (obj.equals("fail")){
		 	return  outData;
		 }
        /*ES 如果发生异常，事务将无法回滚，ES不支持事务，处理方式为先保存操作之前的数据如果ES发生异常，
        则把操作之前的数据重新存入ES，暂时没有实现此功能，日后在改，在编辑修改功能方法中也涉及ES的事务回滚问题*/
//		String updataResult = dataSourceKafkaInterface.updataInsertBaseFilter();// 调用kafka订阅程序更新数据库对比内容
//		if (!"SUCCESS".equals(updataResult)) {
//			message = "远程调用kafka订阅程序更新对比map失败";
//		}
//		String deleteResult = dataSourceSettingService.deleteImpossibleData(deleteId);// 删除模板库中的对应记录
//		if (!"success".equals(deleteResult)) {
//			message = deleteResult;
//		}
	}catch (RuntimeException e){
		    type="fail";
		    message="添加失败";
		    StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
		return outData;

	}
		outData.put("type", type);
		outData.put("message", message);
		return outData;
	}

	/**
	 * @Description 获取所有可能需要的数据的ip TODO
	 * 
	 *              <pre>
	 * @author HuYiWu
	 * 
	 *         <pre>
	 * @date 2018年4月19日 下午3:44:50
	 * 
	 *       <pre>
	 */
	@RequestMapping(value = "/getPossibleNeedDataIpAddr", method = RequestMethod.POST)
	@ResponseBody
	public Object getPossibleNeedDataIpAddr() {
		return dataSourceSettingService.getPossibleNeedDataIpAddr();
	}

	/**
	 * @Description 根据ipAddr获取所有可能需要的数据的sendUser TODO
	 * 
	 *              <pre>
	 * @author HuYiWu
	 * 
	 *         <pre>
	 * @date 2018年4月19日 下午3:44:50
	 * 
	 *       <pre>
	 */
	@RequestMapping(value = "/getPossibleNeedDataSendUserByIpAddr", method = RequestMethod.POST)
	@ResponseBody
	public Object getPossibleNeedDataSendUserByIpAddr(HttpServletRequest request) {
		String ipAddr = request.getParameter("ipAddr");
		return dataSourceSettingService.getPossibleNeedDataSendUserByIpAddr(ipAddr);
	}

	/**
	 * @Description 根据ipAddr获取所有可能需要的数据的sendUser TODO
	 * 
	 *              <pre>
	 * @author HuYiWu
	 * 
	 *         <pre>
	 * @date 2018年4月19日 下午3:44:50
	 * 
	 *       <pre>
	 */
	@RequestMapping(value = "/getPossibleNeedDataFileNameByIpAddrAndSendUser", method = RequestMethod.POST)
	@ResponseBody
	public Object getPossibleNeedDataFileNameByIpAddrAndSendUser(HttpServletRequest request) {
		String ipAddr = request.getParameter("ipAddr");
		String sendUser = request.getParameter("sendUser");
		return dataSourceSettingService.getPossibleNeedDataFileNameByIpAddrAndSendUser(ipAddr, sendUser);
	}

	/**
	 * @Description 根据ipAddr获取所有可能需要的数据的sendUser TODO
	 *
	 *              <pre>
	 * @author HuYiWu
	 *
	 *         <pre>
	 * @date 2018年4月19日 下午3:44:50
	 *
	 *       <pre>
	 */
	@RequestMapping(value = "/getPossibleNeedDataFileNameByIpAddrAndSendUserAndFileName", method = RequestMethod.POST)
	@ResponseBody
	public Object getPossibleNeedDataFileNameByIpAddrAndSendUserAndFileName(HttpServletRequest request) {
		String ipAddr = request.getParameter("ipAddr");
		String sendUser = request.getParameter("sendUser");
		String fileName = request.getParameter("fileName");
		return dataSourceSettingService.getPossibleNeedDataFileNameByIpAddrAndSendUserAndFileName(ipAddr, sendUser,fileName);
	}


	/**
	 * 查询DataSourceSetting
	 */
	@RequestMapping(value = "getDataSourceSettingData")
	@ResponseBody
	public Object getDataSourceSettingData(HttpServletRequest req) {
		// 每页几条,页大小
		String limitS = req.getParameter("limit");
		int limit = Integer.parseInt(limitS);
		// 从第几条开始
		String offsetS = req.getParameter("offset");
		int offset = Integer.parseInt(offsetS);
		return dataSourceSettingService.getDataSourceSettingData(offset, limit);

	}

	/**
	 * 编辑DataSourceSetting    修改
	 */
	@RequestMapping(value = "EditDataSource")
	@ResponseBody

	public Object EditDataSource(HttpServletRequest request) {
		String message="success";
		DataSourceSetting dataSourceSetting = new DataSourceSetting();
		dataSourceSetting.setName(request.getParameter("name"));
		dataSourceSetting.setTimeFormat(request.getParameter("timeFormat"));
		dataSourceSetting.setFileName(request.getParameter("type"));
		dataSourceSetting.setDirectory(request.getParameter("directory"));
		dataSourceSetting.setSendUser(request.getParameter("senderUser"));
		dataSourceSetting.setDataType(request.getParameter("dataType"));
		dataSourceSetting.setIpAddr(request.getParameter("ipAddr"));
		dataSourceSetting.setPhone(request.getParameter("phone"));
		dataSourceSetting.setDepartmentName(request.getParameter("departmentName"));
		dataSourceSetting.setUseDepartment(request.getParameter("useDepartment"));
		dataSourceSetting.setMoniterTimer(request.getParameter("moniterTimer"));
		String pkId = request.getParameter("pkId");
		dataSourceSetting.setPkId(Long.parseLong(pkId));
		String Exitbefore=request.getParameter("Exitbefore");
		ArrayList<DataSourceSetting> arrayList = new ArrayList<DataSourceSetting>();
		JSONArray parseArray = JSONArray.parseArray(Exitbefore);
		for (Object object : parseArray) {
			arrayList.add(JSON.parseObject(JSON.toJSONString(object), DataSourceSetting.class));
		}
		JSONObject resultObj = new JSONObject();
		try{
		    String result = dataSourceSettingService.EditDataSource(dataSourceSetting,arrayList);
			if (result==null||result.equals("")||result.equals("fail")){
				resultObj.put("result", "fail");
				message="fail";
				resultObj.put("message",message);
				return resultObj;
			}
			else {
				resultObj.put("message",message);
				resultObj.put("result", result);
			}
//			String updataResult = dataSourceKafkaInterface.updataInsertBaseFilter();// 调用kafka订阅程序更新数据库对比内容
//			if (!"SUCCESS".equals(updataResult)) {
//				message = "fail";
//				resultObj.put("message",message);
//			}
//			else if ("SUCCESS".equals(updataResult)){
//				resultObj.put("message",message);
//			}

		}
		catch (RuntimeException e){
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			message = "fail";
			resultObj.put("result", "fail");
			resultObj.put("message",message);

			System.out.println("更改失败，事务回滚！"+e.getMessage());
			return resultObj;

		}

		return resultObj;



	}

	@RequestMapping(value = "deleteDataSource")
	@ResponseBody

	public Object deleteDataSource(HttpServletRequest request) {
		String data = request.getParameter("data");
		
		ArrayList<DataSourceSetting> arrayList = new ArrayList<DataSourceSetting>();
		JSONArray parseArray = JSONArray.parseArray(data);
		for (Object object : parseArray) {
			arrayList.add(JSON.parseObject(JSON.toJSONString(object), DataSourceSetting.class));
		}
		JSONObject resultObj = new JSONObject();
		try {
			List<DataInfo> dataInfoList=dataSourceSettingService.deleteDataSource(arrayList);
			String jsondatainfolist=JSON.toJSONString(dataInfoList);
			dataSourceEsInterface.DeletePrepareData(jsondatainfolist);
			resultObj.put("result", "success");
		} catch (RuntimeException e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			resultObj.put("result", "fail");
			System.out.println(e.getMessage());
//              throw new RuntimeException("测试删除事务！");

		}

			return resultObj;



	}

	/**
	 *  再添加元数据配置的时候，根据用户名查询用户目录并返回用户目录并和文件名前段目录拼接
	 */
	@RequestMapping(value = "/finAllUsercatalog" , method = RequestMethod.POST)
	@ResponseBody
	public Object finAllUsercatalog(HttpServletRequest request){
		Map<String, Object> outData = new HashMap<String, Object>();
		String type = "success";
		String message = "成功";
		String user_catalog_name=request.getParameter("User_catalog_name");
		String userfile=request.getParameter("Userfile");
		String user_ip=request.getParameter("User_ip");
		userfile=userfile.trim();
		user_catalog_name=user_catalog_name.trim();
		System.out.println(user_catalog_name);
		System.out.println(user_ip);
		User_Catalog user_catalog=dataSourceSettingService.findAll_User_catalog(user_catalog_name,user_ip);
		if (null==user_catalog){
			outData.put("type", "fail");
			outData.put("message", "用户目录不存在");
			return outData;
		}
		String [] userfilearrray=userfile.split("/");
		String  userfilepath="";
		String lengstr=new String();
		//截取文件路径去除文件名
		for (int i=0;i<userfilearrray.length-1;i++){
			if(lengstr==null||lengstr.length()<userfilearrray[i].length()){
				userfilepath=userfilepath+userfilearrray[i]+"/";
			}
		}
		if(!userfilepath.startsWith("/")){
			userfilepath="/"+userfilepath;
		}
		String user_content= user_catalog.getUser_catalog_content();
		//判断文件名是否是全路径，如果是则不进行拼接直接返回文件名，反之则拼接成全路径
		if(userfilepath.startsWith(user_content)){
			outData.put("type", type);
			outData.put("message", message);
			outData.put("Usercatalog",userfilepath);
			return  outData;
		}
		else {
			//因为不是全路径，所以截取这部分路径和User_catalog表中的用户路径拼接成全路径
			System.out.println("else");
			if(userfilepath.startsWith("/")&&user_content.endsWith("/")){
				System.out.println("去除   /");
				userfilepath=userfilepath.substring(1);
			}
			user_content=user_content+userfilepath;
			outData.put("type", type);
			outData.put("message", message);
			outData.put("Usercatalog",user_content);
			return  outData;
		}
	}
	@RequestMapping(value = "/formatchange", method = RequestMethod.POST)
	@ResponseBody
	public Object formatchange(HttpServletRequest request){
		Map<String, Object> outData = new HashMap<String, Object>();
		String type = "success";
		String message = "成功";
		String filenametxt=request.getParameter("filename");
		System.out.println("时间格式更改文件名one:"+filenametxt);
		String filename=filenametxt;
		if (filename!=null){
			String [] array=filename.split("/");
			filename=array[array.length-1];
		}
		String filenametwo=filename;
		System.out.println("时间格式更改文件名two:"+filename);
		String format=request.getParameter("format");
		System.out.println("需要更改的之后的时间格式："+format);
		String regEx="[^0-9]";
		Pattern pattern=Pattern.compile(regEx);
		Matcher matcher=pattern.matcher(filename);

		String[] newFileNameArray = matcher.replaceAll(" ").trim().split(" ");
		List<String> timeList = new ArrayList<>();
		for (int i = 0; i < newFileNameArray.length; i++) {
			if (newFileNameArray[i].length() >= 8 && !timeList.contains(newFileNameArray[i])){
				timeList.add(newFileNameArray[i]);
			}
		}

//		String newfilename=matcher.replaceAll(" ").trim();
//		String[] newFileNameArray = newfilename.split(" ");
//		String timeStr = new String();
//		for (int i = 0; i < newFileNameArray.length; i++) {
//			String group = newFileNameArray[i];
//			if (timeStr == null || timeStr.length() < group.length()){
//				timeStr = group;
//			}
//		}

		if (timeList.get(0).length()<format.length()){
			outData.put("type","fail");
			outData.put("leng",filenametxt);
			outData.put("message","你选的时间格式和文件时间格式不匹配");
			return outData;
		}
		else {
			for(int i=0;i<timeList.size();i++){
				String mat= "\\d{"+format.length()+"}"+timeList.get(i).substring(format.length());
				filename=filename.replace(timeList.get(i),mat);
			}
			outData.put("type",type);
			outData.put("message",message);
			outData.put("outfilename",filename);
			return  outData;
		}

	}
	
}
