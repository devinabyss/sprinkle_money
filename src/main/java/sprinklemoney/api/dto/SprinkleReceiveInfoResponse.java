package sprinklemoney.api.dto;

import lombok.Builder;
import lombok.Getter;
import sprinklemoney.domain.money.entity.SprinkleReceive;

import java.math.BigDecimal;

@Getter
public class SprinkleReceiveInfoResponse {
    private final BigDecimal receivedAmount;
    private final String receiver;

    @Builder
    public SprinkleReceiveInfoResponse(SprinkleReceive sprinkleReceive) {
        this.receivedAmount = sprinkleReceive.getAmount();
        this.receiver = sprinkleReceive.getReceiver().getKeyValue();
    }
}
