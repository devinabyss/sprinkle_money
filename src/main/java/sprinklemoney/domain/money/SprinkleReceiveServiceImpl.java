package sprinklemoney.domain.money;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import sprinklemoney.common.error.BaseException;
import sprinklemoney.common.error.ErrorStatus;
import sprinklemoney.domain.money.entity.Sprinkle;
import sprinklemoney.domain.money.entity.SprinkleReceive;
import sprinklemoney.domain.money.repository.SprinkleReceiveRepository;
import sprinklemoney.domain.user.entity.User;

import java.util.List;
import java.util.Optional;

@Service
public class SprinkleReceiveServiceImpl implements SprinkleReceiveService {

    private final SprinkleReceiveRepository sprinkleReceiveRepository;

    public SprinkleReceiveServiceImpl(SprinkleReceiveRepository sprinkleReceiveRepository) {
        this.sprinkleReceiveRepository = sprinkleReceiveRepository;
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public SprinkleReceive assignSprinkleReceive(SprinkleReceive receive) {
        return saveSprinkleReceive(receive);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public SprinkleReceive assignSprinkleReceive(Sprinkle sprinkle, User receiver) {

        SprinkleReceive receive = getFirstBySprinkleAndReceiverIsNullOrderById(sprinkle)
                .orElseThrow(() -> new BaseException(ErrorStatus.INVALID_SPRINKLE_STATUS));

        if (getBySprinkleAndReceiver(sprinkle, receiver).isPresent())
            throw new BaseException(ErrorStatus.ALREADY_RECEIVED_SPRINKLE);
        receive.setReceiver(receiver);

        return saveSprinkleReceive(receive);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public SprinkleReceive assignSprinkleReceive(SprinkleReceive receive, Sprinkle sprinkle, User receiver) {
        if (getBySprinkleAndReceiver(sprinkle, receiver).isPresent())
            throw new BaseException(ErrorStatus.ALREADY_RECEIVED_SPRINKLE);

        SprinkleReceive newR = sprinkleReceiveRepository.findById(receive.getId()).orElseThrow(() -> new RuntimeException(""));

        if (newR.getReceiver() != null) {
            throw new RuntimeException("에라이 씨발");
        }

        newR.setReceiver(receiver);

        return saveSprinkleReceive(newR);

    }

    @Override
    @Transactional
    public SprinkleReceive saveSprinkleReceive(SprinkleReceive sprinkleReceive) {
        return sprinkleReceiveRepository.save(sprinkleReceive);
    }

    @Override
    public Optional<SprinkleReceive> getFirstBySprinkleAndReceiverIsNullOrderById(Sprinkle sprinkle) {
        return sprinkleReceiveRepository.findSomething(sprinkle);
    }

    @Override
    public Optional<SprinkleReceive> getBySprinkleAndReceiver(Sprinkle sprinkle, User receiver) {
        return sprinkleReceiveRepository.findBySprinkleAndReceiver(sprinkle, receiver);
    }

    @Override
    public List<SprinkleReceive> getBySprinkleAndReceiverIsNotNull(Sprinkle sprinkle) {
        return sprinkleReceiveRepository.findBySprinkleAndReceiverIsNotNull(sprinkle);
    }


}
