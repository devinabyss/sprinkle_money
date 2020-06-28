package sprinklemoney.domain.money.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter

public class GetSprinkleParameters {

    private final String token;
    private final String roomId;
    private final String userId;

    @Builder
    public GetSprinkleParameters(@NonNull String token, String roomId, String userId) {
        this.token = token;
        this.roomId = roomId;
        this.userId = userId;
    }
}
