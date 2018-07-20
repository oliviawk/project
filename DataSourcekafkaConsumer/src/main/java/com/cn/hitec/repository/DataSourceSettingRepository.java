package com.cn.hitec.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cn.hitec.bean.DataSourceSetting;

public interface DataSourceSettingRepository extends JpaRepository<DataSourceSetting, Long>{
	
}
