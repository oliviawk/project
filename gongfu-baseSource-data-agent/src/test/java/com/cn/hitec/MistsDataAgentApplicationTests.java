package com.cn.hitec;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.cn.hitec.service.GongFuService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MistsDataAgentApplicationTests {

	@Autowired
	GongFuService gongfuService;

	@Test
	public void test() {

		System.out.println("输出正常！");
	}

	@Test
	public void test2() {
		gongfuService.findEventData();
	}
}
