package sprinklemoney.api.dto;

import lombok.Builder;
import lombok.Getter;
import sprinklemoney.domain.money.entity.Sprinkle;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class SprinkleInfoResponse {

    private final LocalDateTime created;
    private final BigDecimal initialAmount;
    private final BigDecimal sprinkledAmount;
    private final List<SprinkleReceiveInfoResponse> receives;

    @Builder
    public SprinkleInfoResponse(Sprinkle sprinkle) {
        this.created = sprinkle.getCreated();
        this.initialAmount = sprinkle.getSprinkleAmount();
        this.receives = sprinkle.getReceives().stream()
                .map(SprinkleReceiveInfoResponse::new).collect(Collectors.toList());
        this.sprinkledAmount = receives.stream()
                .map(SprinkleReceiveInfoResponse::getReceivedAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
