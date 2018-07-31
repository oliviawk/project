package hitec.repository;

import hitec.domain.Alert_strategy;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

/**
 * Created by libin on 2018/7/31.
 */

public interface Alert_strategy_Repository extends JpaRepository<Alert_strategy, Long> {
    @Modifying
    @Transactional
    @Query(value = "DELETE from alert_strategy WHERE  di_id=?1",nativeQuery = true)
    int delectAlert_strategy(Long di_id);
}
