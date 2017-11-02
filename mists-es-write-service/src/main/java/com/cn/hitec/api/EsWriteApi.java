package com.cn.hitec.api;

import com.cn.hitec.bean.EsBean;
import com.cn.hitec.controller.BaseController;
import com.cn.hitec.service.ESClientAdminService;
import com.cn.hitec.service.ESService;
import com.cn.hitec.tools.Pub;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

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
	ESClientAdminService esClientAdminService;
	@Autowired
	ESService esService;


	@RequestMapping(value="/getHealth",method= RequestMethod.GET)
	public Map<String,Object> getHealth(){
		long start = System.currentTimeMillis();
		Map<String,Object> map = esClientAdminService.getClusterHealth();
		long spend = System.currentTimeMillis()-start;
    	outMap.put(KEY_RESULT,VAL_SUCCESS);
    	outMap.put(KEY_RESULTDATA,map);
    	outMap.put(KEY_MESSAGE,"数据获取成功！");
    	outMap.put(KEY_SPEND,spend+"mm");
        return outMap;
	}

	@RequestMapping(value="/add",method=RequestMethod.POST,consumes="application/json")
	public Map<String,Object> add(@RequestBody EsBean esBean){
		if(esBean == null || esBean.getData() == null){
			outMap.put(KEY_RESULT,VAL_ERROR);
			outMap.put(KEY_RESULTDATA,null);
			outMap.put(KEY_MESSAGE,"ES写入数据失败！数据为 null");
			return outMap;
		}
		String index = esBean.getIndex();
		if(StringUtils.isEmpty(index)){
            SimpleDateFormat sdf = new SimpleDateFormat(Pub.Index_Food_Simpledataformat);
            index = Pub.Index_Head+(sdf.format(new Date()));
        }
		long start = System.currentTimeMillis();
		Map<String,Object> map = new HashMap<>();

//		System.out.printf("Json数据 %s",esBean.getData().toString() +"\n");

		int num = esService.insert(index,esBean.getType(),esBean.getData());

		map.put("insert_number",num);
		long spend = System.currentTimeMillis()-start;
		outMap.put(KEY_RESULT,VAL_SUCCESS);
		outMap.put(KEY_RESULTDATA,map);
		outMap.put(KEY_MESSAGE,"数据添加成功");
		outMap.put(KEY_SPEND,spend+"mm");
		return outMap;
	}


    @RequestMapping(value="/update",method=RequestMethod.POST,consumes="application/json")
    public Map<String,Object> update(@RequestBody EsBean esBean){
        if(esBean == null  || esBean.getData() == null || esBean.getData().size() <= 0){
            outMap.put(KEY_RESULT,VAL_ERROR);
            outMap.put(KEY_RESULTDATA,null);
            outMap.put(KEY_MESSAGE,"ES修改数据失败！数据为 null");
            return outMap;
        }
        long start = System.currentTimeMillis();
        Map<String,Object> map = new HashMap<>();
//		System.out.printf("Json数据 %s",esBean.getData().toString() +"\n");

        int num = esService.update(esBean.getIndex(),esBean.getType(),esBean.getData());
        if(num == 0 || esBean.getData().size() > num){
            outMap.put(KEY_MESSAGE,"数据修改失败");
        }else{
            outMap.put(KEY_MESSAGE,"数据修改成功");
        }
        map.put("update_number",num);
        long spend = System.currentTimeMillis()-start;
        outMap.put(KEY_RESULT,VAL_SUCCESS);
        outMap.put(KEY_RESULTDATA,map);

        outMap.put(KEY_SPEND,spend+"mm");
        return outMap;
    }

    @RequestMapping(value="/update2",method=RequestMethod.POST,consumes="application/json")
    public Map<String,Object> update_field(@RequestBody EsBean esBean){
        if(esBean == null || esBean.getIndex() == null || esBean.getType() == null || esBean.getId() == null|| esBean.getParams() == null){
            outMap.put(KEY_RESULT,VAL_ERROR);
            outMap.put(KEY_RESULTDATA,null);
            outMap.put(KEY_MESSAGE,"ES修改数据失败！数据为 null");
            return outMap;
        }
        long start = System.currentTimeMillis();
        Map<String,Object> map = new HashMap<>();
//		System.out.printf("Json数据 %s",esBean.getData().toString() +"\n");

        int num = esService.update_field(esBean.getIndex(),esBean.getType(),esBean.getId(),esBean.getParams());
        if(num > 0){
            outMap.put(KEY_MESSAGE,"数据修改成功");
        }else {
            outMap.put(KEY_MESSAGE,"数据修改失败");
        }
        long spend = System.currentTimeMillis()-start;
        outMap.put(KEY_RESULT,VAL_SUCCESS);
        outMap.put(KEY_SPEND,spend+"mm");
        return outMap;
    }



}
