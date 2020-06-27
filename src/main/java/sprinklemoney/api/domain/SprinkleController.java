package sprinklemoney.api.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sprinklemoney.api.common.BaseController;
import sprinklemoney.api.common.CustomResponse;
import sprinklemoney.api.dto.CreateSprinkleRequest;
import sprinklemoney.domain.money.SprinkleService;
import sprinklemoney.domain.money.dto.CreateReceiveParameters;
import sprinklemoney.domain.money.dto.CreateSprinkleParameters;
import sprinklemoney.domain.money.entity.Sprinkle;
import sprinklemoney.domain.money.entity.SprinkleReceive;

import javax.validation.Valid;

@RestController
@RequestMapping("sprinkle")
public class SprinkleController extends BaseController {

    @Autowired
    private SprinkleService sprinkleService;


    @PostMapping
    public ResponseEntity<CustomResponse> createSprinkle(@RequestHeader("X-USER-ID") String userId, @RequestHeader("X-ROOM-ID") String roomId, @Valid @RequestBody CreateSprinkleRequest request) {

        Sprinkle sprinkle = sprinkleService.createSprinkle(CreateSprinkleParameters.builder()
                .amount(request.getAmount())
                .divideSize(request.getDivideSize())
                .authorId(userId)
                .roomId(roomId)
                .build()
        );

        return success(sprinkle.getToken().getValue());
    }

    @GetMapping
    public ResponseEntity<CustomResponse> readSprinkle(@RequestHeader("X-USER-ID") String userId, @RequestHeader("X-ROOM-ID") String roomId, @PathVariable("token") String token) {

        return success();
    }

    @PostMapping("/{token}/receive")
    public ResponseEntity<CustomResponse> receiveSprinkle(@RequestHeader("X-USER-ID") String userId, @RequestHeader("X-ROOM-ID") String roomId, @PathVariable("token") String token) {


        SprinkleReceive receive = sprinkleService.createReceive(CreateReceiveParameters.builder()
                .receiverId(userId)
                .roomId(roomId)
                .token(token)
                .build()
        );

        return success(receive.getAmount());
    }

}
