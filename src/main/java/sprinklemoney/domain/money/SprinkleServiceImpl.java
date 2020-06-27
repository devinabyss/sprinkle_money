package sprinklemoney.domain.money;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import sprinklemoney.common.error.BaseException;
import sprinklemoney.common.error.ErrorStatus;
import sprinklemoney.domain.money.dto.CreateReceiveParameters;
import sprinklemoney.domain.money.dto.CreateSprinkleParameters;
import sprinklemoney.domain.money.entity.Sprinkle;
import sprinklemoney.domain.money.entity.SprinkleReceive;
import sprinklemoney.domain.money.entity.SprinkleToken;
import sprinklemoney.domain.money.repository.SprinkleReceiveRepository;
import sprinklemoney.domain.money.repository.SprinkleRepository;
import sprinklemoney.domain.user.UserService;
import sprinklemoney.domain.user.entity.User;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class SprinkleServiceImpl implements SprinkleService {

    private final SecureRandom secureRandom = new SecureRandom();

    @Autowired
    private SprinkleTokenService sprinkleTokenService;

    @Autowired
    private UserService userService;

    @Autowired
    private SprinkleRepository sprinkleRepository;

    @Autowired
    private SprinkleReceiveRepository sprinkleReceiveRepository;


    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Sprinkle createSprinkle(CreateSprinkleParameters parameters) {

        User author = userService.getUserWithGenerateByKeyValue(parameters.getAuthorId());

        if (sprinkleRepository.findByAuthorAndCreatedAfter(author, LocalDateTime.now().minus(10, ChronoUnit.MINUTES)).size() > 0)
            throw new BaseException(ErrorStatus.HAVE_AVAILABLE_SPRINKLE);

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

        return sprinkleRepository.save(sprinkle);
    }

    @Override
    @Transactional(readOnly = true)
    public Sprinkle getSprinkleWithReceives(String tokenValue) {
        Optional<Sprinkle> optional = getSprinkle(tokenValue);
        Sprinkle sprinkle = optional.orElseThrow(() -> new BaseException(ErrorStatus.NOT_EXIST_SPRINKLE));
        List<SprinkleReceive> receiveList = sprinkle.getSprinkleReceives();
        log.error("# List : {}", receiveList);

        return sprinkle;
    }


    @Override
    @Transactional(readOnly = true)
    public Optional<Sprinkle> getSprinkle(String tokenValue) {
        Optional<SprinkleToken> tokenOptional = sprinkleTokenService.getSprinkleToken(tokenValue);
        if (tokenOptional.isEmpty())
            throw new BaseException(ErrorStatus.INVALID_SPRINKLE_TOKEN_VALUE);

        return sprinkleRepository.findByToken(tokenOptional.get());
    }

    @Override
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

        SprinkleReceive sprinkleReceive = sprinkle.share(receiver, secureRandom, parameters.getRoomId());

        return sprinkleReceiveRepository.save(sprinkleReceive);
    }
}
