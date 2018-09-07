package com.hitec.repository.jpa;/**
 * Created by libin on 2018/9/4.
 */

import com.hitec.domain.Basesourceuser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @ClassName Basesource_userRepository
 * @Description TODO
 * @Author Li Cong
 * @Date 2018/9/4 11:07
 * @vERSION 1.0
 **/
public interface Basesource_userRepository extends JpaRepository<Basesourceuser,Long> {
    @Transactional
    @Modifying
    @Query(value = "INSERT  INTO basesource_user( id ,`user`,sms,enable)VALUE (?1,?2,?3,?4);",nativeQuery = true)
    public void  Addbasesource(Long id,String user,String sms,Float enable);

    @Query(value = "SELECT * FROM basesource_user WHERE id=?1" ,nativeQuery = true)
     public Basesourceuser queryrules(Long id);

    @Query(value = "select urs.id ,urs.name ,urs.phone, bu.enable from users urs LEFT JOIN basesource_user bu ON urs.name = bu.user; " ,nativeQuery = true)
    public List<Object> queryallrules();


    @Transactional
    @Modifying
    @Query(value = "UPDATE basesource_user SET `user`=?2,sms=?3,enable=?4 WHERE  id=?1" ,nativeQuery = true)
    public void updaterules(Long id ,String user,String phone,Float enable);

}
