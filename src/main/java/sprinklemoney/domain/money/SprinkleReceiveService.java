package sprinklemoney.domain.money;

import sprinklemoney.domain.money.entity.Sprinkle;
import sprinklemoney.domain.money.entity.SprinkleReceive;
import sprinklemoney.domain.user.entity.User;

import java.util.List;
import java.util.Optional;

public interface SprinkleReceiveService {

    SprinkleReceive assignSprinkleReceive(SprinkleReceive receive);

    SprinkleReceive assignSprinkleReceive(Sprinkle sprinkle, User receiver);

    SprinkleReceive assignSprinkleReceive(SprinkleReceive receive, Sprinkle sprinkle, User receiver);

    SprinkleReceive saveSprinkleReceive(SprinkleReceive sprinkleReceive);

    Optional<SprinkleReceive> getFirstBySprinkleAndReceiverIsNullOrderById(Sprinkle sprinkle);

    Optional<SprinkleReceive> getBySprinkleAndReceiver(Sprinkle sprinkle, User receiver);

    List<SprinkleReceive> getBySprinkleAndReceiverIsNotNull(Sprinkle sprinkle);


}
