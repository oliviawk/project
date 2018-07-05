package hitec.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import hitec.domain.DataSourceSetting;

public interface DataSourceSettingRepository extends JpaRepository<DataSourceSetting, Long>{

	@Query(value="select * from data_source_setting limit ?1 , ?2", nativeQuery=true)
	List<DataSourceSetting> findAll(int offset,int limit);
	
	
}
