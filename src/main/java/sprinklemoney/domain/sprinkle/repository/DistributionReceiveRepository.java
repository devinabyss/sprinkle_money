package sprinklemoney.domain.sprinkle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sprinklemoney.domain.sprinkle.entity.DistributionReceive;
import sprinklemoney.domain.sprinkle.entity.Sprinkle;
import sprinklemoney.domain.user.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface DistributionReceiveRepository extends JpaRepository<DistributionReceive, Long> {

    Optional<DistributionReceive> findDistributionReceiveBySprinkleAndReceiver(Sprinkle sprinkle, User receiver);
    
    List<DistributionReceive> findDistributionReceivesBySprinkle(Sprinkle sprinkle);
}
