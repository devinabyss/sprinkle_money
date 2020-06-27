package sprinklemoney.domain.money.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sprinklemoney.domain.money.entity.SprinkleToken;

import java.util.Optional;

@Repository
public interface SprinkleTokenRepository extends JpaRepository<SprinkleToken, Long> {

    Optional<SprinkleToken> findByValue(String value);

    Optional<SprinkleToken> findByValueAndStatus(String value, SprinkleToken.Status status);
}
