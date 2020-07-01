package sprinklemoney.domain.sprinkle.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class CreateSprinkleParameters {

    private final String authorId;
    private final String roomId;
    private final int divideSize;
    private final BigDecimal amount;

    @Builder
    public CreateSprinkleParameters(String authorId, String roomId, int divideSize, BigDecimal amount) {
        this.authorId = authorId;
        this.roomId = roomId;
        this.divideSize = divideSize;
        this.amount = amount;
    }
}
