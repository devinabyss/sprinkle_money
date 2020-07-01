package sprinklemoney.domain.sprinkle;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import sprinklemoney.common.error.BaseException;
import sprinklemoney.common.error.ErrorStatus;
import sprinklemoney.domain.sprinkle.dto.CreateReceiveParameters;
import sprinklemoney.domain.sprinkle.dto.CreateSprinkleParameters;
import sprinklemoney.domain.sprinkle.dto.GetSprinkleParameters;
import sprinklemoney.domain.sprinkle.entity.DistributionReceive;
import sprinklemoney.domain.sprinkle.entity.Sprinkle;
import sprinklemoney.domain.sprinkle.entity.SprinkleToken;
import sprinklemoney.domain.sprinkle.repository.SprinkleRepository;
import sprinklemoney.domain.user.entity.User;
import sprinklemoney.domain.user.service.UserService;

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

        sprinkle.generateDistribution(secureRandom).forEach(sprinkleDistribution -> distributionService.saveDistribution(sprinkleDistribution));

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

        Sprinkle sprinkle = sprinkleRepository.findByToken(token).orElseThrow(() -> new BaseException(ErrorStatus.NOT_EXIST_SPRINKLE));

        User receiver = userService.getUserWithGenerateByKeyValue(parameters.getReceiverId());

        return distributionService.saveDistributionReceive(sprinkle.distributeToReceiver(distributionService, receiver, parameters.getRoomId()));
    }

}
