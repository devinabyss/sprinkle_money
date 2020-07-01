package sprinklemoney.domain.sprinkle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sprinklemoney.domain.sprinkle.entity.SprinkleToken;

import java.util.Optional;

@Repository
public interface SprinkleTokenRepository extends JpaRepository<SprinkleToken, Long> {

    Optional<SprinkleToken> findByValue(String value);
}
