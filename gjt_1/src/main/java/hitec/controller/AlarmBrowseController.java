package hitec.controller;

import javax.servlet.http.HttpServletRequest;

import hitec.service.AlarmBrowseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/alarmBrowse")
public class AlarmBrowseController {

    @Autowired
    AlarmBrowseService alarmBrowseService;
    
    @RequestMapping("/")
    public String findDatasByIsData(){
        System.out.println("进入MICAPS4.html");
        return "index";
    }
    
    /**
     * @Description: TODO(获取左侧分类节点) 
     * @author HYW
     * @date 2018年3月13日 上午10:57:38 
     * @return
     */
    @RequestMapping(value="/getAlarmType", method=RequestMethod.POST)
    @ResponseBody
    public Object getAlarmType(){
        Object alarmTypeNode = alarmBrowseService.getAlarmTypeNode();
        return alarmTypeNode;
    }
    
    /**
     * 
    * @Description 方法描述: 获取所有告警事件 <pre>
    * @return  返回值类型: 返回所有告警<pre>  
    * @author 作者: HuYiWu <pre>
    * @date 时间: 2018年3月21日 下午3:28:58 <pre>
     */
    @RequestMapping(value="/getAllAlarm", method=RequestMethod.POST)
    @ResponseBody
    public Object getAllAlarm(HttpServletRequest request){
    	String intervalTimeStr = request.getParameter("intervalTimeStr");
    	String showType = request.getParameter("showType");
    	return alarmBrowseService.getAllAlarm(intervalTimeStr, showType);
    }
    
}
