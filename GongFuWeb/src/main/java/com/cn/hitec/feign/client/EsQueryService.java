package com.cn.hitec.feign.client;


import com.cn.hitec.bean.EsQueryBean;
import com.cn.hitec.bean.EsQueryBean_Exsit;
import com.cn.hitec.bean.EsQueryBean_web;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/** 
 * 
 * 
 * @description: TODO(这里用一句话描述这个类的作用) 
 * @author james
 * @since 2017年7月20日 下午2:41:56 
 * @version 
 *
 */
@FeignClient("mists-es-query-service")
public interface EsQueryService {

	/**
	 * 检测es健康状态， 测试
	 * @return
	 */
	@RequestMapping(value="/esapi/getHealth",method=RequestMethod.GET,consumes="application/json")
	public String getESHealth();

    /**
     * 时效图 数据查询
     * @param esQueryBean
     * @return
     * @throws Exception
     */
    @RequestMapping(value="/query/getdata",method= RequestMethod.POST , consumes = "application/json")
    public Map<String,Object> getData(@RequestBody EsQueryBean esQueryBean) throws  Exception;

    /**
     * 流程图 最新查询方法，添加must、mustNot、range等
     * @param esQueryBean
     * @return
     * @throws Exception
     */
    @RequestMapping(value="/query/getdata_new",method= RequestMethod.POST , consumes = "application/json")
    public Map<String,Object> getData_new(@RequestBody EsQueryBean esQueryBean) throws Exception;

    /**
     * @return
     */
    @RequestMapping(value="/query/getalert",method=RequestMethod.POST,consumes="application/json")
    public Map<String,Object> getAlertData(@RequestBody EsQueryBean esQueryBean);


    @RequestMapping(value="/query/indexIsExist",method= RequestMethod.POST,consumes="application/json")
    public Map<String,Object> indexIsExist(@RequestBody EsQueryBean_Exsit esQueryBean);


    /**
     * 聚合查询流程图各环节状态
     * 2018.05.18  fukl
     * @return
     */
    @RequestMapping(value = "/query/lctAggQuery", method = RequestMethod.POST, consumes = "application/json")
    public Map<String, Object> lctAggQuery(@RequestBody EsQueryBean_web  esQueryBean);


    @RequestMapping(value = "/query/findDataByQuery", method = RequestMethod.POST, consumes = "application/json")
    public Map<String, Object> findDataByQuery(@RequestBody EsQueryBean esQueryBean);


    @RequestMapping(value = "/query/getFileSizeCount", method = RequestMethod.POST, consumes = "application/json")
    public List<Object> getFileSizeCount(@RequestBody  String str);

}
