package com.hitec.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.hitec.domain.DataInfo;

public interface DataInfoRepository extends JpaRepository<DataInfo,Long> {

    @Query(value="SELECT * FROM `data_info` WHERE is_data = 1  LIMIT 0, 1000;", nativeQuery = true)
    List<DataInfo> initData();

    /*@Query(value="select id,`name`,`module`,is_data,timeout_threshold,sub_name,should_time,regular,monitor_times,file_size_define,file_name_define,ip from data_info where parent_id = ?1", nativeQuery = true)*/
    @Query(value="select d.id,`name`,`module`,`is_data`,timeout_threshold,sub_name,should_time,regular,monitor_times,file_size_define,file_name_define,ip,beforeAlert,delayAlert,alertTimeRange,maxAlerts from alert_rules a RIGHT JOIN (SELECT * FROM data_info WHERE parent_id = ?1) d on a.id=d.id", nativeQuery = true)
    List<Object> initSelected(int parent_id);

    @Transactional
    @Modifying
    /*@Query(value="UPDATE alert_module_copy RIGHT JOIN (SELECT id,service_type,module,`name`,ip FROM data_info WHERE id=?5) d ON module_key=CONCAT('OP_',service_type,'_',module,',',d.name,',',ip) SET beforeAlert=?1,delayAlert=?2,alertTimeRange=?3,maxAlerts=?4", nativeQuery = true)*/
    @Query(value="INSERT INTO alert_rules(id,beforeAlert,delayAlert,alertTimeRange,maxAlerts,currentAlerts) VALUES(?5,?1,?2,?3,?4,0) ON DUPLICATE KEY UPDATE beforeAlert=VALUES(beforeAlert),delayAlert=VALUES(delayAlert),alertTimeRange=VALUES(alertTimeRange),maxAlerts=VALUES(maxAlerts),currentAlerts=0", nativeQuery = true)
    void updateAlertRules(Integer beforeAlert,Integer delayAlert,String alertTimeRange,Integer maxAlerts,long id);

    @Query(value="select id from data_info where parent_id = ?1", nativeQuery = true)
    List<Object> findId(int parent_id);

    @Transactional
    @Modifying
    @Query(value="UPDATE data_info SET regular=?2,timeout_threshold=?3,should_time=?4,monitor_times=?5 " +
            ",file_size_define=?6,file_name_define=?7 " +
            "WHERE id = ?1", nativeQuery = true)
    void updateWhereId(long id,int regular,String timeout,String shouldTime,String monitorTimes,String fileSizeDefine,String fileNameDefine);


    @Query(value = "select * from " +
            "(select * from data_info where FIND_IN_SET(id, getChildLst(?1)) and is_data = 1 ) din," +
            "alert_strategy als where als.di_id = din.id;", nativeQuery = true)
    List<DataInfo> findAll( long id );

    @Query(value = "select * from data_info where  is_data = 1 and id = ?1 ;", nativeQuery = true)
    DataInfo findAllById(long id);


    @Transactional
    @Modifying
    @Query(value="UPDATE data_info SET alert_level=?1,timeout_threshold=?2  WHERE id IN (?3);", nativeQuery = true)
    void updateWhereIds(int level,String timeout,Long... ids);

    @Query(value="select id from data_info where parent_id=1 and name=?1",nativeQuery=true)
    String ensureIpExit(String ip);
    
    @Modifying
    @Transactional
    @Query(value="delete from data_info where parent_id=?1",nativeQuery=true)
    void deleteExitIpBaseSource(String id);
    
    @Query(value="select id from data_info where parent_id=1",nativeQuery=true)
    List findIdMaxValue();
    
    
}
