package com.cn.hitec.service;

import com.cn.hitec.bean.EsQueryBean;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ESConfigServiceTest {
    @Autowired
    ESConfigService esConfigService;

    @Test
    public void getConfigAlert() throws  Exception{
        EsQueryBean esQueryBean = new EsQueryBean();
        esQueryBean.setIndices(new String[]{"config"});
        esQueryBean.setTypes(new String[]{"collect"});
        List<Map> list = esConfigService.getConfigAlert(esQueryBean.getIndices(),esQueryBean.getTypes(),esQueryBean.getParameters());

        for (Map<String,Object> map : list){
            System.out.println(map.toString());
        }
    }

}