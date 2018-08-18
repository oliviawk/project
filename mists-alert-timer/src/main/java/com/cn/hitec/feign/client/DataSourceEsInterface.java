package com.cn.hitec.feign.client;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;


@FeignClient("mists-es-write-service")
public interface DataSourceEsInterface {

    /**
     * 生成节目表
     * @param json
     * @return
     */
    @RequestMapping(value = "/datasource/insertDataSource_DI", method = RequestMethod.POST, consumes = "application/json")
    public Map<String, Object> insertDataSource_DI(@RequestBody String json);

    /**
     * 数据入库
     * @param json
     * @return
     */
    @RequestMapping(value = "/datasource/insertDataSource", method = RequestMethod.POST, consumes = "application/json")
    public Map<String, Object> insertDataSource(@RequestBody String json);


}
