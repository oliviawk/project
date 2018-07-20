package com.hitec.repository.jpa;

import com.hitec.domain.SendTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Description: 查询模板查询类
 * @author: fukl
 * @data: 2018年02月07日 14:59
 */
public interface SendTemplateRepository extends JpaRepository<SendTemplate,Long>{
    /**
     * 查询所有
     * @return
     */
    List<SendTemplate> findAll();


    @Transactional
    @Modifying
    @Query(value="UPDATE SysDataCompleteProcessInfo SET Id = ?1 WHERE fileType = ?2 AND fileName = ?3")
    void updateFileId(long newSortPkId, String fileType, String ctsName);

    @Transactional
    @Modifying
    @Query(value="UPDATE send_template SET name = ?2  ,type = ?3 ,wechart_content_template=?4 ,wechart_send_enable=?5 ,sms_content_template=?6 ,sms_send_enable=?7 WHERE id = ?1", nativeQuery = true)
    void updateWhereId(long updId, String a,String b,String c,String d,String e,String f);



    @Query(value="select * from send_template where 1=1 " +
            "AND  " +
            "IF(?1 = '' , 1=1 , `name` = ?1) " +
            "AND " +
            "IF(?2 = '' , 1=1 , `type`= ?2)"+
            "AND  " +
            "IF(?3 = '' , 1=1 , wechart_content_template = ?3) " +
            "AND  " +
            "IF(?4 = '' , 1=1 , sms_content_template = ?4) " , nativeQuery = true)
    List<SendTemplate> findTemp(String name,String type,String wechartContentTemplate,String sms_content_template);

}