package com.hitec.controller;

import com.hitec.domain.SysDataCompleteProcessInfo;
import com.hitec.repository.jpa.SysDataCompleteProcessInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @ClassName: SysDataCompleteProcessController 
 * @Description: TODO(获取 数据全流程 配置信息的类) 
 * @author HYW
 * @date 2017年10月19日 下午2:28:23 
 *
 */
@RestController
@RequestMapping("/sysDataCompleteProcess")
public class SysDataCompleteProcessController extends BaseController{

    @Autowired
    SysDataCompleteProcessInfoRepository sysDataCompleteProcessInfoRepository;
    

    /**
     * 获取所有监视的资料
     * @return
     */
    @RequestMapping(value="/getAllByIsMonitor",method= RequestMethod.GET)
    public Map<String,Object> getAllByIsMonitor(){
        long start = System.currentTimeMillis();
        Map<String,Object> outMap = new HashMap<String,Object>();
        List<SysDataCompleteProcessInfo> list = sysDataCompleteProcessInfoRepository.findByIsImportant(1);
//        List<SysDataCompleteProcessInfo> list = sysDataCompleteProcessInfoRepository.findByIsMonitor(1);
        long spend = System.currentTimeMillis()-start;
        outMap.put(KEY_RESULT,VAL_SUCCESS);
        outMap.put(KEY_RESULTDATA,list);
        outMap.put(KEY_MESSAGE,"数据获取成功！");
        outMap.put(KEY_SPEND,spend+"mm");
        return outMap;
    }
}
