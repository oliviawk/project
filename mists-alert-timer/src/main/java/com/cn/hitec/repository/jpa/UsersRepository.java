package com.cn.hitec.repository.jpa;

import com.cn.hitec.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @Description: 查询数据类
 * @author: fukl
 * @data: 2018年03月5日 14:59
 */
public interface UsersRepository extends JpaRepository<Users,Long> {

    /**
     * 查询所有
     * @return
     */
    List<Users> findAll();


    @Query(value = "SELECT  * FROM  users WHERE id IN ?1 ;",nativeQuery = true)
    List<Users> findAllByIds(long... ids);

    @Query(value = "SELECT  * FROM  users WHERE parent_id = ?1 ;",nativeQuery = true)
    List<Users> findAllByPid(long id);

    @Query(value = "SELECT  * FROM  users WHERE is_user = 0 ;",nativeQuery = true)
    List<Users> findAllData();

}
