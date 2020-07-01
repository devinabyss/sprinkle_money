package sprinklemoney.api.dto;

import lombok.Builder;
import lombok.Getter;
import sprinklemoney.domain.sprinkle.entity.DistributionReceive;

import java.math.BigDecimal;

@Getter
public class SprinkleReceiveInfoResponse {
    private final BigDecimal receivedAmount;
    private final String receiver;

    @Builder
    public SprinkleReceiveInfoResponse(DistributionReceive receive) {
        this.receivedAmount = receive.getDistribution().getAmount();
        this.receiver = receive.getReceiver().getKeyValue();
    }
}
