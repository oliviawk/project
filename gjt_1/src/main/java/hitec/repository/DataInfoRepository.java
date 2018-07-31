package hitec.repository;

import java.util.List;

import hitec.domain.DataInfo;

import org.hibernate.annotations.SQLDelete;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

public interface DataInfoRepository extends JpaRepository<DataInfo, Long>{
    
    @Query(value="from DataInfo where isData=?1 order by id")
    List<DataInfo> findDatasByIsData(int isData);

    @Query(value="from DataInfo where parentId=?1 order by id")
    List<DataInfo> findDatasByParentId(long parentId);

    @Modifying
    @Transactional
    @Query(value=" DELETE from data_info  WHERE   name=?  AND  ip=?  AND  file_name_define=?  AND file_path=? ",nativeQuery = true)
    int deletedatainfo(String name,String ip,String file_name_define,String file_path);

    @Query(value="select id from data_info  WHERE  name=?1  AND  ip=?2  AND  file_name_define=?3  AND file_path=?4 ",nativeQuery = true)
    long  findDatainfoID(String name,String ip,String file_name_define,String file_path);

}
