package sprinklemoney.domain.money.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sprinklemoney.domain.money.entity.Sprinkle;
import sprinklemoney.domain.money.entity.SprinkleDistribution;

import java.util.Optional;

@Repository
public interface SprinkleDistributionRepository extends JpaRepository<SprinkleDistribution, Long> {


    //dist.sprinkle.id = :#{#sprinkle.id} AND
    @Query("SELECT dist From SprinkleDistribution dist WHERE dist.id = " +
            "(SELECT MIN(dist.id) FROM SprinkleDistribution dist WHERE dist.sprinkle.id = :#{#sprinkle.id} AND NOT EXISTS (SELECT receive FROM DistributionReceive receive WHERE receive.distribution.id = dist.id))")
    Optional<SprinkleDistribution> getDistributeAvailable(@Param("sprinkle") Sprinkle sprinkle);

}
