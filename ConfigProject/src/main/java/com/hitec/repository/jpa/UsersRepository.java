package com.hitec.repository.jpa;

import com.hitec.domain.SendTemplate;
import com.hitec.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UsersRepository extends JpaRepository<Users,Long> {

    List<Users> findAll();

    @Query(value = "SELECT * FROM  users WHERE  name=?1",nativeQuery = true)
    List<Users> SelectUserPhone (String  username);

    @Query(value = "select * from users where  is_user = 1 order by parent_id desc;",nativeQuery = true)
    List<Users> findAllParent();

    @Query(value = "select * from users where  is_user = 0 order by parent_id desc;",nativeQuery = true)
    List<Users> findAllis_user();

//    @Transactional
//    @Modifying
//    @Query(value="UPDATE SysDataCompleteProcessInfo SET Id = ?1 WHERE fileType = ?2 AND fileName = ?3")
//    void updateFileId(long newSortPkId, String fileType, String ctsName);
    @Query(value="select * from users where 1=1 " +
            "AND  " +
            "IF(?1 = 0 , 1=1 , parent_id = ?1) " +
            "AND " +
            "IF(?2 = '' , 1=1 , `name`= ?2)"+
            "AND  " +
            "IF(?3 = '' , 1=1 , phone = ?3) " +
            "AND  " +
            "IF(?4 = '' , 1=1 , wechart = ?4) " , nativeQuery = true)
    List<Users> findUser(int parent_id,String name, String phone, String wechart);


    @Query(value = "select us.* , parentSelect.`name` pname from users us , " +
            "(select name,id from users where is_user = 1  AND   IF(?1 = '' , 1=1 , `id` = ?1) ) parentSelect  " +
            "where  us.parent_id = parentSelect.id  " +
            "AND " +
            "IF(?2 = '' , 1=1 , us.`name`= ?2)"+
            "AND  " +
            "IF(?3 = '' , 1=1 , us.phone = ?3) " +
            "AND  " +
            "IF(?4 = '' , 1=1 , us.wechart = ?4)" +
            "order by parent_id desc", nativeQuery = true)
    List<Object> findUsers_pname(String parent_name,String name, String phone, String wechart);

}
