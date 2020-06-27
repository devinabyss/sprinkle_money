package sprinklemoney.domain.money.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sprinklemoney.domain.money.entity.SprinkleReceive;

@Repository
public interface SprinkleReceiveRepository extends JpaRepository<SprinkleReceive, Long> {
}
