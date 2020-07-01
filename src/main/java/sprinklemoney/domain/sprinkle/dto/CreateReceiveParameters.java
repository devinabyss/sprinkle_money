package sprinklemoney.domain.sprinkle.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CreateReceiveParameters {

    private final String receiverId;
    private final String roomId;
    private final String token;

    @Builder
    public CreateReceiveParameters(String receiverId, String roomId, String token) {
        this.receiverId = receiverId;
        this.roomId = roomId;
        this.token = token;
    }
}
