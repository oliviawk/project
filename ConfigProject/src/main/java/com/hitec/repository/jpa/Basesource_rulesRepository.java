package com.hitec.repository.jpa;/**
 * Created by libin on 2018/9/4.
 */

import com.hitec.domain.Basesourcerules;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @ClassName Basesource_rulesRepository
 * @Description TODO
 * @Author Li Cong
 * @Date 2018/9/4 11:06
 * @vERSION 1.0
 **/
public interface Basesource_rulesRepository extends JpaRepository<Basesourcerules,Long> {
    @Transactional
    @Modifying
    @Query(value = "INSERT  INTO basesource_rules(maxAlerts,alertTimeRange,`interval`)VALUE (?1,?2,?3);",nativeQuery = true)
    public int  Addbasesource(Integer max,String time,Integer  interval);

    @Query(value = "SELECT * FROM basesource_rules" ,nativeQuery = true)
    public List<Object> queryrules();


    @Transactional
    @Modifying
    @Query(value = "UPDATE basesource_rules SET maxAlerts=?2,alertTimeRange=?3,`interval`=?4 WHERE  id=?1" ,nativeQuery = true)
    public void updaterules(Long id,Integer maxalert,String alerttimerange,Integer interval);


}
