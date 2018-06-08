package com.hitec.controller;

import com.hitec.domain.SendTemplate;
import com.hitec.repository.jpa.SendTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 发布模板
 * @author: fukl
 * @data: 2018年02月07日 15:11
 */
@RestController
@RequestMapping("/sendTemplate")
public class SendTemplateController extends BaseController{

    @Autowired
    SendTemplateRepository sendTemplateRepository;

    /**
     * 查询所有
     * @return
     */
    @RequestMapping(value = "/getAll" ,method = RequestMethod.GET)
    public Map<String,Object> getAll(){
        long start = System.currentTimeMillis();
        Map<String,Object> outMap = new HashMap<String,Object>();
        List<SendTemplate> list = sendTemplateRepository.findAll();
        long spend = System.currentTimeMillis()-start;
        outMap.put(KEY_RESULT,VAL_SUCCESS);
        outMap.put(KEY_RESULTDATA,list);
        outMap.put(KEY_MESSAGE,"请求成功！");
        outMap.put(KEY_SPEND,spend+"mm");
        return outMap;
    }

}

