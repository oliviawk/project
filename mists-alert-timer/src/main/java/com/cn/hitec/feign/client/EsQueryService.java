package com.cn.hitec.feign.client;


import com.cn.hitec.bean.EsQueryBean;
import com.cn.hitec.bean.EsQueryBean_Exsit;
import com.cn.hitec.bean.EsWriteBean;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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
	 * @return
	 */
	@RequestMapping(value="/query/getalert",method=RequestMethod.POST,consumes="application/json")
	public Map<String,Object> getAlertData(@RequestBody EsQueryBean esQueryBean);

    @RequestMapping(value="/query/getdata",method= RequestMethod.POST,consumes="application/json")
    public Map<String,Object> getData(@RequestBody EsQueryBean esQueryBean);

    @RequestMapping(value="/query/getdata_new",method= RequestMethod.POST,consumes="application/json")
    public Map<String,Object> getData_new(@RequestBody EsQueryBean esQueryBean);

    @RequestMapping(value="/query/getdata_resultId",method= RequestMethod.POST,consumes="application/json")
    public Map<String,Object> getData_resultId(@RequestBody EsQueryBean esQueryBean) throws  Exception;

    @RequestMapping(value="/query/indexIsExist",method= RequestMethod.POST,consumes="application/json")
    public Map<String,Object> indexIsExist(@RequestBody EsQueryBean_Exsit esQueryBean);


    @RequestMapping(value = "/query/getDocumentById", method = RequestMethod.POST, consumes = "application/json")
    public String getDocumentById(@RequestBody String json);
}
