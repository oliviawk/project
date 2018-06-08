package com.cn.hitec.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@ActiveProfiles({"dev"})
@RunWith(SpringRunner.class)
@SpringBootTest
public class FZJCSendConsumerTest {

    @Autowired
    DataSourceSendConsumer dataSourceSendConsumer;

    @Test
    public void insert_leida_Data(){
    	dataSourceSendConsumer.consume();
//    	dataSourceSendConsumer.makeProjectTable(new Date());
//    	dataSourceSendConsumer.processing("");
    	
    }
}