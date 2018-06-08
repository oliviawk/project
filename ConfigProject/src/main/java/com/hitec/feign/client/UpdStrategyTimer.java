package com.hitec.feign.client;


import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


/**
 *
 *
 * @description: TODO(这里用一句话描述这个类的作用)
 * @author fukl
 * @since 2018年3月26日 下午2:41:56
 * @version
 *
 */
@FeignClient("mists-alert-timer")
public interface UpdStrategyTimer {
    /**
     * @return
     */
    @RequestMapping(value = "/updStrategyApi/initMap", method = RequestMethod.POST, consumes = "application/json")
    String updInitMap(@RequestBody String json);
}
