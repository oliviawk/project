package com.cn.hitec.repository;

import com.cn.hitec.bean.User_Catalog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by libin on 2018/6/22.
 */
public interface User_Catalog_Repository extends JpaRepository<User_Catalog,String> {

    @Query(value="select * from User_Catalog WHERE user_name=?", nativeQuery=true )
     User_Catalog   findAll_User_catalog(String user_name);
}
