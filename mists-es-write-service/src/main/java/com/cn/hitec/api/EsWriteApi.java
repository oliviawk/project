package com.cn.hitec.api;

import java.text.SimpleDateFormat;
import java.util.*;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cn.hitec.bean.*;
import com.cn.hitec.controller.BaseController;
import com.cn.hitec.service.ESClientAdminService;
import com.cn.hitec.service.ESService;
import com.cn.hitec.tools.Pub;

/**
 * 
 * 
 * @description: TODO(这里用一句话描述这个类的作用)
 * @author fukl
 * @since 2017年8月3日
 * @version
 *
 */
@RestController
@RequestMapping("/write")
public class EsWriteApi extends BaseController {
	@Autowired
	ESService esService;


	@RequestMapping(value = "/add", method = RequestMethod.POST, consumes = "application/json")
	public Map<String, Object> add(@RequestBody EsBean esBean) {
		if (esBean == null || esBean.getData() == null || StringUtils.isEmpty(esBean.getType())) {
			outMap.put(KEY_RESULT, VAL_ERROR);
			outMap.put(KEY_RESULTDATA, null);
			outMap.put(KEY_MESSAGE, "ES写入数据失败！数据为 null");
			return outMap;
		}
		// System.out.println(JSON.toJSONString(esBean));
		String index = esBean.getIndex();
		if (StringUtils.isEmpty(index)) {
			SimpleDateFormat sdf = new SimpleDateFormat(Pub.Index_Food_Simpledataformat);
			index = Pub.Index_Head + (sdf.format(new Date()));
		}

		long start = System.currentTimeMillis();
		Map<String, Object> map = new HashMap<>();

		// System.out.printf("Json数据 %s",esBean.getData().toString() +"\n");

		int num = esService.add(index, esBean.getType(), esBean.getId(),esBean.getData());

		map.put("insert_number", num);
		long spend = System.currentTimeMillis() - start;
		outMap.put(KEY_RESULT, VAL_SUCCESS);
		outMap.put(KEY_RESULTDATA, map);
		outMap.put(KEY_MESSAGE, "数据添加成功");
		outMap.put(KEY_SPEND, spend + "mm");
		return outMap;
	}

	//无规律数据写入
	@RequestMapping(value = "/insertIrregulardata", method = RequestMethod.POST, consumes = "application/json")
	public Map<String, Object> insertIrregulardata(@RequestBody EsBean esBean) {
		if (esBean == null || esBean.getData() == null || StringUtils.isEmpty(esBean.getType())) {
			outMap.put(KEY_RESULT, VAL_ERROR);
			outMap.put(KEY_RESULTDATA, null);
			outMap.put(KEY_MESSAGE, "ES写入数据失败！数据为 null");
			return outMap;
		}
		System.out.println("ESbean传入！！"+esBean.getData());
		List<String> Irregulartime=esBean.getData();
		System.out.println("write中："+Irregulartime.size());
		Map<String, Object> map = new HashMap<>();
		String  str_type=esBean.getType();
		String index=esBean.getIndex();

		System.out.println("写入：index:"+index+"type:"+str_type);
		int num=0;
		try {
			num=esService.addIrregularservicedata(index,str_type,Irregulartime);
		}catch (Exception e){
			System.out.println("esService中addIrregularservicedata异常");
		}

		if (num == 0 || esBean.getData().size() > num) {
			outMap.put(KEY_MESSAGE, "数据修改失败");
		} else {
			outMap.put(KEY_MESSAGE, "数据修改成功");
		}
		long start = System.currentTimeMillis();
		long spend = System.currentTimeMillis() - start;
		map.put("insert_number", num);
		outMap.put(KEY_RESULT, VAL_SUCCESS);
		outMap.put(KEY_RESULTDATA, map);

		outMap.put(KEY_SPEND, spend + "mm");
		return outMap;
	}


	@RequestMapping(value = "/insert", method = RequestMethod.POST, consumes = "application/json")
	public Map<String, Object> insert(@RequestBody EsBean esBean) {
		if (esBean == null || esBean.getData() == null) {
			outMap.put(KEY_RESULT, VAL_ERROR);
			outMap.put(KEY_RESULTDATA, null);
			outMap.put(KEY_MESSAGE, "ES写入数据失败！数据为 null");
			return outMap;
		}
		String index = esBean.getIndex();
		if (StringUtils.isEmpty(index)) {
			SimpleDateFormat sdf = new SimpleDateFormat(Pub.Index_Food_Simpledataformat);
			index = Pub.Index_Head + (sdf.format(new Date()));
		}
		long start = System.currentTimeMillis();
		Map<String, Object> map = new HashMap<>();

		// System.out.printf("Json数据 %s",esBean.getData().toString() +"\n");

		int num = esService.insert(index, esBean.getType(), esBean.getData());

		map.put("insert_number", num);
		long spend = System.currentTimeMillis() - start;
		outMap.put(KEY_RESULT, VAL_SUCCESS);
		outMap.put(KEY_RESULTDATA, map);
		outMap.put(KEY_MESSAGE, "数据添加成功");
		outMap.put(KEY_SPEND, spend + "mm");
		return outMap;
	}

	@RequestMapping(value = "/insert1", method = RequestMethod.POST, consumes = "application/json")
	public Map<String, Object> insert1(@RequestBody EsBean esBean) {
		if (esBean == null || esBean.getData() == null) {
			outMap.put(KEY_RESULT, VAL_ERROR);
			outMap.put(KEY_RESULTDATA, null);
			outMap.put(KEY_MESSAGE, "ES写入数据失败！数据为 null");
			return outMap;
		}
		String index = esBean.getIndex();
		if (StringUtils.isEmpty(index)) {
			SimpleDateFormat sdf = new SimpleDateFormat(Pub.Index_Food_Simpledataformat);
			index = Pub.Index_Head + (sdf.format(new Date()));
		}
		long start = System.currentTimeMillis();
		Map<String, Object> map = new HashMap<>();

		// System.out.printf("Json数据 %s", esBean.getData().toString() + "\n");

		int num = esService.insert1(index, esBean.getData());
		map.put("insert_number", num);
		long spend = System.currentTimeMillis() - start;
		outMap.put(KEY_RESULT, VAL_SUCCESS);
		outMap.put(KEY_RESULTDATA, map);
		outMap.put(KEY_MESSAGE, "数据添加成功");
		outMap.put(KEY_SPEND, spend + "mm");
		return outMap;
	}

	@RequestMapping(value = "/update", method = RequestMethod.POST, consumes = "application/json")
	public Map<String, Object> update(@RequestBody EsBean esBean) {
		if (esBean == null || esBean.getData() == null || esBean.getData().size() <= 0) {
			outMap.put(KEY_RESULT, VAL_ERROR);
			outMap.put(KEY_RESULTDATA, null);
			outMap.put(KEY_MESSAGE, "ES修改数据失败！数据为 null");
			return outMap;
		}
		long start = System.currentTimeMillis();
		Map<String, Object> map = new HashMap<>();
		// System.out.printf("Json数据 %s",esBean.getData().toString() +"\n");

		int num = esService.update(esBean.getIndex(), esBean.getType(), esBean.getData());
		if (num == 0 || esBean.getData().size() > num) {
			outMap.put(KEY_MESSAGE, "数据修改失败");
		} else {
			outMap.put(KEY_MESSAGE, "数据修改成功");
		}
		map.put("update_number", num);
		long spend = System.currentTimeMillis() - start;
		outMap.put(KEY_RESULT, VAL_SUCCESS);
		outMap.put(KEY_RESULTDATA, map);

		outMap.put(KEY_SPEND, spend + "mm");
		return outMap;
	}

	@RequestMapping(value = "/update2", method = RequestMethod.POST, consumes = "application/json")
	public Map<String, Object> update_field(@RequestBody EsBean esBean) {
		if (esBean == null || esBean.getIndex() == null || esBean.getType() == null || esBean.getId() == null
				|| esBean.getParams() == null) {
			outMap.put(KEY_RESULT, VAL_ERROR);
			outMap.put(KEY_RESULTDATA, null);
			outMap.put(KEY_MESSAGE, "ES修改数据失败！数据为 null");
			return outMap;
		}
		long start = System.currentTimeMillis();
		Map<String, Object> map = new HashMap<>();
		// System.out.printf("Json数据 %s",esBean.getData().toString() +"\n");

		int num = esService.update_field(esBean.getIndex(), esBean.getType(), esBean.getId(), esBean.getParams());
		if (num > -1) {
			outMap.put(KEY_MESSAGE, "数据修改成功");
			outMap.put(KEY_RESULT, VAL_SUCCESS);
		} else {
			outMap.put(KEY_MESSAGE, "数据修改失败");
			outMap.put(KEY_RESULT, VAL_ERROR);
		}
		long spend = System.currentTimeMillis() - start;
		outMap.put(KEY_SPEND, spend + "mm");
		return outMap;
	}

	@RequestMapping(value="/getId",method=RequestMethod.POST,consumes="application/json")
	public Map<String,Object> getDocumentId1(@RequestBody SearchBean esBean){
		outMap=esService.getDocumentId1(esBean.getIndex(),esBean.getType(),esBean.getParams());
		return outMap;
	}

	// esService.add("data_20180206","LAPS",id,JSON.toJSONString(resultMap));

	@RequestMapping(value="/updatebyid",method=RequestMethod.POST,consumes="application/json")
	public Map<String,Object> add(@RequestBody UpdatebyIdBean esbean ){
		esService.add(esbean.getIndex(),esbean.getType(), esbean.getId(),esbean.getJson());
		return outMap;
	}
	//因数据源md5加密更改，为了避免影响其他业务，代码上唯一不同在于MD5的加密参数不同
	@RequestMapping(value = "/updateDataSource", method = RequestMethod.POST, consumes = "application/json")
	public Map<String, Object> updateDataSource(@RequestBody EsBean esBean) {
		if (esBean == null || esBean.getData() == null || esBean.getData().size() <= 0) {
			outMap.put(KEY_RESULT, VAL_ERROR);
			outMap.put(KEY_RESULTDATA, null);
			outMap.put(KEY_MESSAGE, "ES修改数据失败！数据为 null");
			return outMap;
		}
		long start = System.currentTimeMillis();
		Map<String, Object> map = new HashMap<>();
		// System.out.printf("Json数据 %s",esBean.getData().toString() +"\n");

		int num = esService.updateDataSource(esBean.getIndex(), esBean.getType(), esBean.getData());
		if (num == 0 || esBean.getData().size() > num) {
			outMap.put(KEY_MESSAGE, "数据修改失败");
		} else {
			outMap.put(KEY_MESSAGE, "数据修改成功");
		}
		map.put("update_number", num);
		long spend = System.currentTimeMillis() - start;
		outMap.put(KEY_RESULT, VAL_SUCCESS);
		outMap.put(KEY_RESULTDATA, map);

		outMap.put(KEY_SPEND, spend + "mm");
		return outMap;
	}
}
