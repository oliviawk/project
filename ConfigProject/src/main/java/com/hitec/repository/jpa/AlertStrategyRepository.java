package com.hitec.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.hitec.domain.AlertStrategy;

public interface AlertStrategyRepository extends JpaRepository<AlertStrategy,Long> {

    @Override
	List<AlertStrategy> findAll();

    /*@Query(value = "select dt.service_type,dt.name,dt.module,als.strategy_name ,als.wechart_send_enable,als.sms_send_enable,als.wechart_content,als.sms_content" +
            " from data_info dt , alert_strategy als " +
            " where dt.id = als.di_id and dt.id = ?1",nativeQuery = true)*/
    @Query(value="select dt.service_type,dt.name,dt.module,als.strategy_name ,als.wechart_send_enable,als.sms_send_enable,als.wechart_content,als.sms_content from alert_strategy als RIGHT JOIN (SELECT * FROM data_info WHERE parent_id = ?1) dt ON dt.id = als.di_id",nativeQuery = true)
    List<Object> findDesc(long id);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM alert_strategy WHERE di_id = ?1",nativeQuery = true)
    void deleteByDi_id(long di_id);

    @Modifying
    @Transactional
    @Query(value="delete from alert_strategy where di_id like ?1",nativeQuery=true)
    void deleteExitIpBaseSourceAlert(String id);

    @Transactional
    @Modifying
    @Query(value = "UPDATE FROM alert_strategy SET wechart_content = ?2 , sms_content = ?3 WHERE template_id = ?1 ",nativeQuery = true)
    void updateDataTemplate(int template_id, String wechart_content, String sms_content);
    
}
