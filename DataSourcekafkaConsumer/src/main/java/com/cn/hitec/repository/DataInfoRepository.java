package com.cn.hitec.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.cn.hitec.bean.DataInfo;

public interface DataInfoRepository extends JpaRepository<DataInfo, Long>{
    
    @Query(value="select * from DataInfo where parentId=?1 order by id" , nativeQuery = true)
    List<DataInfo> findDatasByParentId(long parentId);
    
    @Query(value="select name from data_info where parent_id=?1 order by id", nativeQuery = true)
    List<String> findNamesByParentId(long parentId);

}
