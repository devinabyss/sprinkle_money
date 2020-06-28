package sprinklemoney.domain.money;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import sprinklemoney.common.error.BaseException;
import sprinklemoney.domain.money.entity.DistributionReceive;
import sprinklemoney.domain.money.entity.Sprinkle;
import sprinklemoney.domain.money.entity.SprinkleDistribution;
import sprinklemoney.domain.money.repository.DistributionReceiveRepository;
import sprinklemoney.domain.money.repository.SprinkleDistributionRepository;
import sprinklemoney.domain.user.entity.User;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class SprinkleDistributionService {

    @Autowired
    private SprinkleDistributionRepository distributionRepository;

    @Autowired
    private DistributionReceiveRepository receiveRepository;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Optional<SprinkleDistribution> getAvailableDistribution(Sprinkle sprinkle) {
        return distributionRepository.getDistributeAvailable(sprinkle);
    }


    public List<DistributionReceive> findDistributionReceives(Sprinkle sprinkle) {
        return receiveRepository.findDistributionReceivesBySprinkle(sprinkle);
    }

    @Transactional
    public SprinkleDistribution saveDistribution(SprinkleDistribution distribution) {
        return distributionRepository.save(distribution);
    }

    @Transactional
    public DistributionReceive saveDistributionReceive(DistributionReceive receive) {
        try {
            return receiveRepository.save(receive);
        } catch (DataIntegrityViolationException e) {
            throw new BaseException.DistributionReceiveHeavyLoadException(receive.getSprinkle(), receive.getDistribution(), e);
        }
    }

    public Optional<DistributionReceive> findReceivedHistory(Sprinkle sprinkle, User user) {
        return receiveRepository.findDistributionReceiveBySprinkleAndReceiver(sprinkle, user);
    }

}
