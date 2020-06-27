package sprinklemoney.domain.money;

import sprinklemoney.domain.money.dto.CreateReceiveParameters;
import sprinklemoney.domain.money.dto.CreateSprinkleParameters;
import sprinklemoney.domain.money.entity.SprinkleReceive;
import sprinklemoney.domain.money.entity.Sprinkle;

public interface SprinkleService {


    Sprinkle createSprinkle(CreateSprinkleParameters parameters);


    SprinkleReceive createReceive(CreateReceiveParameters parameters);





}
