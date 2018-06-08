package com.hitec.repository.jpa;

import java.util.List;

import com.hitec.domain.SysDataCompleteProcessInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface SysDataCompleteProcessInfoRepository extends JpaRepository<SysDataCompleteProcessInfo, Long>{

    List<SysDataCompleteProcessInfo> findByIsImportant(int isImportant);

    SysDataCompleteProcessInfo findDataByPkId(long pkId);

    @Query(value="SELECT * FROM sys_data_complete_process_info WHERE file_type = ?1 GROUP BY sort_pk_id", nativeQuery = true)
    List<SysDataCompleteProcessInfo> findByFileType(String fileType);

    List<SysDataCompleteProcessInfo> findAll();

    @Query(value="SELECT sortId, fileType FROM SysDataCompleteProcessInfo GROUP BY sortId")
    List<Object> findFileType();

    @Transactional
    @Modifying
    @Query(value="UPDATE SysDataCompleteProcessInfo SET sortId = ?1 WHERE fileType = ?2")
    void updateFileTypeSortId(long newSortId, String fileType);

    @Transactional
    @Modifying
    @Query(value="UPDATE SysDataCompleteProcessInfo SET sortPkId = ?1 WHERE fileType = ?2 AND fileName = ?3")
    void updateFileSortPkId(long newSortPkId, String fileType, String ctsName);

    @Query(value="SELECT * FROM sys_data_complete_process_info WHERE file_name = ?1 AND file_type = ?2", nativeQuery = true)
    List<SysDataCompleteProcessInfo> findDataByCtsNameAndFileType(String ctsName, String fileType);

    @Query(value="SELECT * from sys_data_complete_process_info WHERE PK_ID IN (?1)", nativeQuery = true)
    List<SysDataCompleteProcessInfo> findAllDataInPkIds(List<Long> pkIds);
}
