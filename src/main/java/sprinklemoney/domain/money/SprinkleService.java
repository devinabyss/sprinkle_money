package sprinklemoney.domain.money;

import sprinklemoney.domain.money.dto.CreateReceiveParameters;
import sprinklemoney.domain.money.dto.CreateSprinkleParameters;
import sprinklemoney.domain.money.entity.SprinkleReceive;
import sprinklemoney.domain.money.entity.Sprinkle;

import java.util.Optional;

public interface SprinkleService {


    Sprinkle createSprinkle(CreateSprinkleParameters parameters);

    Sprinkle getSprinkleWithReceives(String tokenValue);

    Optional<Sprinkle> getSprinkle(String tokenValue);


    SprinkleReceive createReceive(CreateReceiveParameters parameters);





}
