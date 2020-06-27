package sprinklemoney.domain.money.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sprinklemoney.domain.money.entity.Sprinkle;
import sprinklemoney.domain.money.entity.SprinkleToken;
import sprinklemoney.domain.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SprinkleRepository extends JpaRepository<Sprinkle, Long> {

    Optional<Sprinkle> findByToken(SprinkleToken token);

    Optional<Sprinkle> findByTokenAndCreatedAfter(SprinkleToken token, LocalDateTime condition);

    List<Sprinkle> findByAuthor(User author);

    List<Sprinkle> findByAuthorAndCreatedAfter(User author, LocalDateTime condition);


}
