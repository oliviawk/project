package com.cn.hitec.service;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.cn.hitec.bean.EsQueryBean;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ESConfigServiceTest {
	@Autowired
	ESConfigService esConfigService;

	@Test
	public void getConfigAlert() throws Exception {
		EsQueryBean esQueryBean = new EsQueryBean();
		esQueryBean.setIndices(new String[] { "config" });
		List<Map> list = esConfigService.getConfigAlert(esQueryBean.getIndices(), esQueryBean.getParameters());

		for (Map<String, Object> map : list) {
			System.out.println(map.toString());
		}
	}

}