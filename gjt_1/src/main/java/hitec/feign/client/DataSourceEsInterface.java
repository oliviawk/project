package hitec.feign.client;

import java.util.Map;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient("mists-es-write-service")
public interface DataSourceEsInterface {

	@RequestMapping(value = "/datasource/deletebyid", method = RequestMethod.POST, consumes = "application/json")
	public Map<String, Object> deleteByid(@RequestBody String json);


}
