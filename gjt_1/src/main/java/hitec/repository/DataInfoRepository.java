package hitec.repository;

import java.util.List;

import hitec.domain.DataInfo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DataInfoRepository extends JpaRepository<DataInfo, Long>{
    
    @Query(value="from DataInfo where isData=?1 order by id")
    List<DataInfo> findDatasByIsData(int isData);

}
