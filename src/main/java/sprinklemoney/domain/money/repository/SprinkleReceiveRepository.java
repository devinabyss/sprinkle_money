package sprinklemoney.domain.money.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sprinklemoney.domain.money.entity.Sprinkle;
import sprinklemoney.domain.money.entity.SprinkleReceive;
import sprinklemoney.domain.user.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface SprinkleReceiveRepository extends JpaRepository<SprinkleReceive, Long> {

    Optional<SprinkleReceive> findFirstBySprinkleAndReceiverIsNullOrderById(Sprinkle sprinkle);

    Optional<SprinkleReceive> findBySprinkleAndReceiver(Sprinkle sprinkle, User receiver);

    List<SprinkleReceive> findBySprinkleAndReceiverIsNotNull(Sprinkle sprinkle);

    @Query(
            "SELECT main FROM SprinkleReceive main WHERE main.id = (SELECT MIN(sub.id) FROM SprinkleReceive sub WHERE sub.sprinkle.id = :#{#sprinkle.id} AND sub.receiver.id IS NULL)")
    Optional<SprinkleReceive> findSomething(@Param("sprinkle") Sprinkle sprinkle);
}
