import com.cn.hitec.bean.EsBean;
import com.cn.hitec.feign.client.EsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
@RunWith(SpringRunner.class)
@SpringBootTest
public class ConsumerTest {

    @Autowired
    EsService esService;
    @Test
    public void insertES(){
        EsBean esBean = new EsBean();
        esBean.setType("LAPS");
        List<String> list = new ArrayList<>();
        list.add("{\"type\":\"LSX\",\"occur_time\":1514339320518,\"receive_time\":1514339320518,\"fields\":{\"start_time\":\"2017-12-26 10:06:04.590+0800\",\"end_time\":\"2017-12-26 10:21:51.561+0800\",\"data_time\":\"2017-12-27 08:00:00.000+0000\",\"file_name\":\"-1\",\"file_size\":\"-1\",\"event_status\":\"Major\",\"event_info\":\"test\",\"ip_addr\":\"10.30.16.242\",\"module\":\"采集\",\"step\":\"1\"}}");
        esBean.setData(list);
        String responst1 = esService.add(esBean);
        System.out.println("responst1:"+responst1);

        list.clear();
        list.add("{\"type\":\"L1S\",\"occur_time\":1514339320518,\"receive_time\":1514339320518,\"fields\":{\"start_time\":\"2017-12-26 10:06:04.590+0800\",\"end_time\":\"2017-12-26 10:21:51.561+0800\",\"data_time\":\"2017-12-27 08:00:00.000+0000\",\"file_name\":\"-1\",\"file_size\":\"-1\",\"event_status\":\"Major\",\"event_info\":\"test\",\"ip_addr\":\"10.30.16.242\",\"module\":\"采集\",\"step\":\"1\"}}");
        esBean.setData(list);
        String responst2 = esService.add(esBean);
        System.out.println("responst2:"+responst2);

        list.clear();
        list.add("{\"type\":\"GR2\",\"occur_time\":1514339320518,\"receive_time\":1514339320518,\"fields\":{\"start_time\":\"2017-12-26 10:06:04.590+0800\",\"end_time\":\"2017-12-26 10:21:51.561+0800\",\"data_time\":\"2017-12-27 08:00:00.000+0000\",\"file_name\":\"-1\",\"file_size\":\"-1\",\"event_status\":\"Major\",\"event_info\":\"test\",\"ip_addr\":\"10.30.16.242\",\"module\":\"采集\",\"step\":\"1\"}}");
        esBean.setData(list);
        String responst3 = esService.add(esBean);
        System.out.println("responst3:"+responst3);
    }
}
