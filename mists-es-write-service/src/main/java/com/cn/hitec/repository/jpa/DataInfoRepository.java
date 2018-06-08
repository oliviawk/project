package com.cn.hitec.repository.jpa;

import com.cn.hitec.domain.DataInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @Description: 查询数据类
 * @author: fukl
 * @data: 2018年03月5日 14:59
 */
public interface DataInfoRepository extends JpaRepository<DataInfo,Long> {

    /**
     * 查询所有
     * @return
     */
    List<DataInfo> findAll();


    @Query(value = "SELECT  * FROM data_info WHERE  is_data = 1 AND  parent_id > 30000 AND  should_time > 0 ",nativeQuery = true)
    List<DataInfo> findAll_isData();

    @Query( value = "select dt.service_type,dt.`name` ,dt.module,dt.ip,als.strategy_name," +
            "als.wechart_send_enable,als.wechart_content,als.sms_send_enable,als.sms_content ,als.send_users " +
            "from data_info dt , alert_strategy als " +
            "where dt.id = als.di_id "
            ,nativeQuery = true)
    List<Object> findDataStrategyAll();

}
