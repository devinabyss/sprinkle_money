package sprinklemoney.api.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sprinklemoney.api.common.BaseController;
import sprinklemoney.api.common.CustomResponse;
import sprinklemoney.api.dto.CreateSprinkleRequest;
import sprinklemoney.api.dto.SprinkleInfoResponse;
import sprinklemoney.domain.money.SprinkleService;
import sprinklemoney.domain.money.dto.CreateReceiveParameters;
import sprinklemoney.domain.money.dto.CreateSprinkleParameters;
import sprinklemoney.domain.money.dto.GetSprinkleParameters;
import sprinklemoney.domain.money.entity.DistributionReceive;
import sprinklemoney.domain.money.entity.Sprinkle;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

@Validated
@RestController
@RequestMapping("sprinkle")
public class SprinkleController extends BaseController {

    @Autowired
    private SprinkleService sprinkleService;


    @PostMapping
    public ResponseEntity<CustomResponse> createSprinkle(@Valid @NotEmpty @RequestHeader("X-USER-ID") String userId, @Valid @NotEmpty @RequestHeader("X-ROOM-ID") String roomId, @Valid @RequestBody CreateSprinkleRequest request) {

        Sprinkle sprinkle = sprinkleService.createSprinkle(CreateSprinkleParameters.builder()
                .amount(request.getAmount())
                .divideSize(request.getDivideSize())
                .authorId(userId)
                .roomId(roomId)
                .build()
        );

        return success(sprinkle.getToken().getValue());
    }

    @GetMapping("{token}")
    public ResponseEntity<CustomResponse> readSprinkle(@NotEmpty @RequestHeader("X-USER-ID") String userId, @NotEmpty @RequestHeader("X-ROOM-ID") String roomId, @PathVariable("token") String token) {

        Sprinkle sprinkle = sprinkleService.getSprinkleWithReceives(GetSprinkleParameters.builder().token(token).roomId(roomId).userId(userId).build());


        return success(SprinkleInfoResponse.builder().sprinkle(sprinkle).build());
    }

    @PostMapping("/{token}/receive")
    public ResponseEntity<CustomResponse> receiveSprinkle(@RequestHeader("X-USER-ID") String userId, @RequestHeader("X-ROOM-ID") String roomId, @PathVariable("token") String token) {


        DistributionReceive receive = sprinkleService.assignDistribution(CreateReceiveParameters.builder()
                .receiverId(userId)
                .roomId(roomId)
                .token(token)
                .build()
        );

        return success(receive.getDistribution().getAmount());
    }

}
