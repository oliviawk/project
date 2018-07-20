package com.cn.hitec.repository.jpa;

import com.cn.hitec.domain.DataInfo;
import com.cn.hitec.domain.SendTemplate;
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
public interface SendTemplateRepository extends JpaRepository<SendTemplate,Long> {

    /**
     * 查询所有
     * @return
     */
    List<SendTemplate> findAll();


    @Query(value = "SELECT  * FROM send_template  WHERE  id  = ?1 ",nativeQuery = true)
    SendTemplate findOneById(long id);


}
