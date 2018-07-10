package hitec.feign.client;


import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient("data-source-kafka-service")
public interface DataSourceKafkaInterface {

	@RequestMapping(value = "/dataSourceKafka/updataInsertBaseFilter", method = RequestMethod.POST, consumes = "application/json")
	public String updataInsertBaseFilter();

}
