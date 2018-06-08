package com.cn.hitec.service;

import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

@Service
public class LAPSCollectConsumer extends MsgConsumer {
	private static final Logger logger = LoggerFactory.getLogger(LAPSCollectConsumer.class);
    private static String topic = "LAPS";
	private static String type = "LAPS";

    public LAPSCollectConsumer(@Value("${LAPS.group.id}")String group) {
		super(topic,group,type);
    }

	@Override
    public List<String> processing(String msg){
    	List<String> toEsJsons = new ArrayList<>();
//		IP type_subtype module_step begin end data path size status info
//		10.30.16.242 LAPS_CMISS 采集_1 2017-12-20 02:00:16.578+0800 2017-12-20 02:00:50.403+0800 2017-12-19 12:00:00.000+0000 /mnt/laps_nfs/laps_3x3/t639//gmf.639.2017121912000.grb2 45905310 Fatal t639文件大小异常!
//		10.30.16.242 LAPS_CMISS 采集_1 2017-12-20 02:04:46.453+0800 2017-12-20 02:04:48.489+0800 2017-12-19 18:00:00.000+0000 /home/laps/data/rawdata/aws//aws20171219180000.dat 3892376 OK 自动站数据获取成功!
//		10.30.16.242 LAPS_CMISS 运行_1 2017-12-20 02:05:50.810+0800 2017-12-20 02:22:53.732+0800 2017-12-19 18:00:00.000+0000 /home/laps/laps_data/lapsprd/gr2/173531800.gr2 15417214 OK gr2产品生成成功!
//		10.30.16.242 LAPS_CMISS 运行_1 2017-12-20 02:05:50.810+0800 2017-12-20 02:22:53.732+0800 2017-12-19 18:00:00.000+0000 /home/laps/laps_data/lapsprd/l1s/173531800.l1s 54785176 OK l1s产品生成成功!
//		10.30.16.242 LAPS_CMISS 运行_1 2017-12-20 02:05:50.810+0800 2017-12-20 02:22:53.732+0800 2017-12-19 18:00:00.000+0000 /home/laps/laps_data/lapsprd/lsx/173531800.lsx 328693168 OK lsx产品生成成功!
//		10.30.16.242 LAPS_CMISS 检验_1 2017-12-20 02:22:53.817+0800 2017-12-20 02:22:57.845+0800 2017-12-20 02:00:00.000+0800 /home/laps/laps_data/verif_data/surface/space_r/laps_aws/1712200200.000 438746 OK 检验成功!
		String[] arr = msg.trim().split("\\s+");
		String[] types = arr[1].split("_");
		String[] minfo = arr[2].split("_");

		JSONObject obj = new JSONObject();
		JSONObject sub = new JSONObject();


		obj.put("type",types[1]);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
		Date endTime = null;
		String begin = arr[3] +" "+ arr[4];
		String end = arr[5] +" "+ arr[6];
		String data_time = arr[7] +" "+ arr[8];
		String ms = end.substring(end.indexOf("."),end.indexOf("+"));
		end = end.replace(ms, StringUtils.rightPad(ms,4,"0"));
		try {
			endTime = df.parse(end);

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		obj.put("occur_time", endTime.getTime());
		obj.put("receive_time", new Date().getTime());

		ms = begin.substring(begin.indexOf("."),begin.indexOf("+"));
		begin = begin.replace(ms, StringUtils.rightPad(ms,4,"0"));
		sub.put("start_time",begin);

		sub.put("end_time",end);

		ms = data_time.substring(data_time.indexOf("."),data_time.indexOf("+"));
		data_time = data_time.replace(ms, StringUtils.rightPad(ms,4,"0"));
		try {
			sub.put("data_time",df.format(df.parse(data_time)));
		} catch (ParseException e) {
			e.printStackTrace();
		}

		sub.put("file_name",arr[9]);
		sub.put("file_size",arr[10]);
		sub.put("event_status",arr[11]);
		sub.put("event_info",arr[12]);
		sub.put("ip_addr",arr[0]);
		sub.put("module",minfo[0]);
		sub.put("step",minfo[1]);

		obj.put("fields",sub);

		logger.info(obj.toString());
		toEsJsons.add(obj.toString());

    	return toEsJsons;
    }

}