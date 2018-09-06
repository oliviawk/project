package com.hitec.repository.jpa;

import com.hitec.domain.DataSourceSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by libin on 2018/8/30.
 */
public interface DataSourceSettingRepository extends JpaRepository<DataSourceSetting,Long> {

    @Query(value = "select * from  data_source_setting  where name=?1" , nativeQuery = true )
    public  DataSourceSetting queryDataSourceSettingConfig(String strname);
}
