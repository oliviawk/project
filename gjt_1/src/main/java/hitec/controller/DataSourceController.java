package hitec.controller;

import hitec.service.DataSourceService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/dataSource")
public class DataSourceController {

	@Autowired
	DataSourceService dataSourceService;
	
	@RequestMapping("/")
	public String index(){
		return "dataSource/dataSource";
	}
	
	/**
	* @Description 获取所有表格数据 TODO
	* @author HuYiWu
	* @date 2018年3月29日 下午3:03:28
	 */
	@RequestMapping(value="/getAllTableData", method=RequestMethod.POST)
	@ResponseBody
	public Object getAllTableData(HttpServletRequest request){
		return dataSourceService.getAllTableData(request);
	}
	
	/**
	* @Description 获取所有表格数据 TODO
	* @author HuYiWu
	* @date 2018年3月29日 下午3:03:28
	 */
	@RequestMapping(value="/getTableData", method=RequestMethod.POST)
	@ResponseBody
	public Object getTableData(HttpServletRequest request){
		Map<String, Object> outMap = new HashMap<String, Object>();
		List<Map<String, Object>> rows = new ArrayList<Map<String,Object>>();
		
		int total = 5;
		for (int i = 0; i < total; i++) {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("department_name", "气象中心1");
			data.put("name", "国内精细化城镇预报1");
			data.put("aging_status", "正常1");
			rows.add(data);
		}
		outMap.put("total", total);
		outMap.put("rows", rows);
		return outMap;
	}
	
}
