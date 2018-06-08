package com.hitec.controller;

import com.hitec.repository.jpa.AlertStrategyRepository;
import com.hitec.repository.jpa.DataInfoRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * Created by CHENMOHAN on 2018/5/31.
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class StrategyTest {

    @Autowired
    DataInfoRepository dataInfoRepository;
    @Autowired
    AlertStrategyRepository alertStrategyRepository;

    @Test
    public void test1() {
        List<Object> datalist = dataInfoRepository.initSelected(30010001);
        System.out.println(datalist.size());
    }
}
