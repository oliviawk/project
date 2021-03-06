package com.cn.hitec.repository.jpa;

import com.cn.hitec.domain.DataInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

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


    @Query(value = "SELECT  * FROM data_info WHERE  is_data = ?1 AND  parent_id > ?2 AND  should_time > ?3 ",nativeQuery = true)
    List<DataInfo> findAll_isData(int isData, int parentId, int shouldTime);

    @Query(value = "select * from data_info where FIND_IN_SET(id, getChildLst(?1)) and is_data = 1;", nativeQuery = true)
    List<DataInfo> findAllChilden( long id );


    @Query( value = "select dt.service_type,dt.`name` ,dt.module,dt.ip,als.strategy_name," +
                            "als.wechart_send_enable,als.wechart_content,als.sms_send_enable,als.sms_content ,als.send_users,dt.regular,als.template_id " +
                        "from data_info dt , alert_strategy als " +
                        "where dt.id = als.di_id "
            ,nativeQuery = true)
    List<Object> findDataStrategyAll();


    @Query(value="select * from DataInfo where parentId=?1 order by id", nativeQuery = true)
    List<DataInfo> findDatasByParentId(long parentId);

    @Query(value="select name from data_info where parent_id=?1 order by id", nativeQuery = true)
    List<String> findNamesByParentId(long parentId);


    @Query( value = "SELECT module_key,module_key_parent from alert_module ;" ,nativeQuery = true)
    List<Object> findAlertModule();

    @Query( value = "SELECT id,alertTimeRange,maxAlerts,currentAlerts FROM alert_rules RIGHT JOIN (SELECT id as fid FROM data_info WHERE service_type=?1 and module=?2 and  name=?3 and ip=?4) t ON id=fid" ,nativeQuery = true)
    List<Object> findAlertRules(String service_type, String module, String name, String ip);

    @Query( value = "SELECT module_key from alert_module_copy where FIND_IN_SET(id,getPreAlerts(?1)) ;" ,nativeQuery = true)
    List<Object> findPreModules(String module_key);

    @Transactional
    @Modifying
    @Query(value = "UPDATE alert_rules SET currentAlerts=currentAlerts+1 WHERE id = ?1",nativeQuery = true)
    void addAlertCnt(long id);
}
