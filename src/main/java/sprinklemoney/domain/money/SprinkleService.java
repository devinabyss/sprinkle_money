package sprinklemoney.domain.money;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import sprinklemoney.common.error.BaseException;
import sprinklemoney.common.error.ErrorStatus;
import sprinklemoney.domain.money.dto.CreateReceiveParameters;
import sprinklemoney.domain.money.dto.CreateSprinkleParameters;
import sprinklemoney.domain.money.dto.GetSprinkleParameters;
import sprinklemoney.domain.money.entity.DistributionReceive;
import sprinklemoney.domain.money.entity.Sprinkle;
import sprinklemoney.domain.money.entity.SprinkleReceive;
import sprinklemoney.domain.money.entity.SprinkleToken;
import sprinklemoney.domain.money.repository.SprinkleRepository;
import sprinklemoney.domain.user.UserService;
import sprinklemoney.domain.user.entity.User;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Slf4j
@Service
public class SprinkleService {

    private final SecureRandom secureRandom = new SecureRandom();

    @Autowired
    private SprinkleTokenService sprinkleTokenService;

    @Autowired
    private UserService userService;

    @Autowired
    private SprinkleRepository sprinkleRepository;

//    @Autowired
//    private SprinkleReceiveRepository sprinkleReceiveRepository;

    @Autowired
    private SprinkleReceiveService sprinkleReceiveService;

    @Autowired
    private SprinkleDistributionService distributionService;


    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Sprinkle createSprinkle(CreateSprinkleParameters parameters) {

        User author = userService.getUserWithGenerateByKeyValue(parameters.getAuthorId());

        if (sprinkleRepository.findByAuthorAndCreatedAfter(author, LocalDateTime.now().minus(10, ChronoUnit.MINUTES)).size() > 0)
            throw new BaseException(ErrorStatus.ALREADY_HAD_RUNNING_SPRINKLE);

        SprinkleToken token = sprinkleTokenService.generateSprinkleToken();

        Sprinkle sprinkle = Sprinkle.builder()
                .author(author)
                .token(token)
                .sprinkleAmount(parameters.getAmount())
                .divideSize(parameters.getDivideSize())
                .roomId(parameters.getRoomId())
                .build();

        token.setStatus(SprinkleToken.Status.LINKED);

        sprinkleTokenService.saveSprinkleToken(token);

        sprinkle.distributeWhenSprinkleCreated(secureRandom).forEach(sprinkleDistribution -> distributionService.saveDistribution(sprinkleDistribution));

        return sprinkleRepository.save(sprinkle);
    }


    @Transactional(readOnly = true)
    public Sprinkle getSprinkleWithReceives(GetSprinkleParameters parameters) {
        Sprinkle sprinkle = getSprinkle(parameters.getToken())
                .filter(exist ->
                        exist.getCreated().isAfter(LocalDateTime.now().minus(7, ChronoUnit.DAYS))
                                && StringUtils.equals(parameters.getRoomId(), exist.getRoomId())
                                && StringUtils.equals(parameters.getUserId(), exist.getAuthor().getKeyValue())
                )
                .orElseThrow(() -> new BaseException(ErrorStatus.NOT_EXIST_SPRINKLE));

        sprinkle.setReceives(distributionService.findDistributionReceives(sprinkle));
        return sprinkle;
    }


    @Transactional(readOnly = true)
    public Optional<Sprinkle> getSprinkle(String tokenValue) {
        Optional<SprinkleToken> tokenOptional = sprinkleTokenService.getSprinkleToken(tokenValue);
        if (tokenOptional.isEmpty())
            throw new BaseException(ErrorStatus.INVALID_SPRINKLE_TOKEN_VALUE);

        return sprinkleRepository.findByToken(tokenOptional.get());
    }


    @Retryable(include = BaseException.DistributionReceiveHeavyLoadException.class)
    @Transactional
    public DistributionReceive assignDistribution(CreateReceiveParameters parameters) {

        SprinkleToken token = sprinkleTokenService.getSprinkleToken(parameters.getToken()).orElseThrow(() -> new BaseException(ErrorStatus.INVALID_SPRINKLE_TOKEN_VALUE));

        User receiver = userService.getUserWithGenerateByKeyValue(parameters.getReceiverId());

        Sprinkle sprinkle = sprinkleRepository.findByToken(token).orElseThrow(() -> new BaseException(ErrorStatus.NOT_EXIST_SPRINKLE));

        return sprinkle.distributeToReceiver(distributionService, receiver, parameters.getRoomId());
    }


    @Deprecated
    @Transactional
    public SprinkleReceive assignReceive(CreateReceiveParameters parameters) {

        SprinkleToken token = sprinkleTokenService.getSprinkleToken(parameters.getToken()).orElseThrow(() -> new BaseException(ErrorStatus.INVALID_SPRINKLE_TOKEN_VALUE));

        User receiver = userService.getUserWithGenerateByKeyValue(parameters.getReceiverId());

        Optional<Sprinkle> sprinkleOptional = sprinkleRepository.findByToken(token);

        if (sprinkleOptional.isEmpty())
            throw new BaseException(ErrorStatus.NOT_EXIST_SPRINKLE);

        Sprinkle sprinkle = sprinkleOptional.get();

        if (!parameters.getRoomId().equals(sprinkle.getRoomId()))
            throw new BaseException(ErrorStatus.NOT_ELIGIBLE);

        if (sprinkleReceiveService.getBySprinkleAndReceiver(sprinkle, receiver).isPresent())
            throw new BaseException(ErrorStatus.ALREADY_RECEIVED_SPRINKLE);

        SprinkleReceive receive = sprinkleReceiveService.getFirstBySprinkleAndReceiverIsNullOrderById(sprinkle)
                .orElseThrow(() -> new BaseException(ErrorStatus.INVALID_SPRINKLE_STATUS));

        log.info("## Receive: {}", receive);

//        if (sprinkleReceiveService.getBySprinkleAndReceiver(sprinkle, receiver).isPresent())
//            throw new BaseException(ErrorStatus.ALREADY_RECEIVED_SPRINKLE);
        log.info("###### before set");
        receive.setReceiver(receiver);


        return receive;//sprinkleReceiveService.assignSprinkleReceive(receive);
    }


    @Deprecated
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public SprinkleReceive createReceive(CreateReceiveParameters parameters) {

        Optional<SprinkleToken> tokenOptional = sprinkleTokenService.getSprinkleToken(parameters.getToken());

        if (tokenOptional.isEmpty())
            throw new BaseException(ErrorStatus.INVALID_SPRINKLE_TOKEN_VALUE);

        User receiver = userService.getUserWithGenerateByKeyValue(parameters.getReceiverId());

        SprinkleToken token = tokenOptional.get();

        Optional<Sprinkle> sprinkleOptional = sprinkleRepository.findByToken(token);

        if (sprinkleOptional.isEmpty())
            throw new BaseException(ErrorStatus.NOT_EXIST_SPRINKLE);

        Sprinkle sprinkle = sprinkleOptional.get();

        SprinkleReceive sprinkleReceive = sprinkle.shareLogicWhenReceiveRequested(receiver, secureRandom, parameters.getRoomId());

        return sprinkleReceiveService.saveSprinkleReceive(sprinkleReceive);
    }
}
