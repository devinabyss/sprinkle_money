package sprinklemoney.domain.money;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import sprinklemoney.common.error.BaseException;
import sprinklemoney.domain.common.util.StringHandleUtil;
import sprinklemoney.domain.money.entity.SprinkleToken;
import sprinklemoney.domain.money.repository.SprinkleTokenRepository;

import java.util.Optional;

@Slf4j
@Service
public class SprinkleTokenServiceImpl implements SprinkleTokenService {

    private final SprinkleTokenRepository sprinkleTokenRepository;

    public SprinkleTokenServiceImpl(SprinkleTokenRepository sprinkleTokenRepository) {
        this.sprinkleTokenRepository = sprinkleTokenRepository;
    }


    @Override
    public Optional<SprinkleToken> getSprinkleToken(String tokenValue) {
        return sprinkleTokenRepository.findByValue(tokenValue);
    }

    @Override
    @Retryable(include = {DataIntegrityViolationException.class})
    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
    public SprinkleToken generateSprinkleToken() {
        String value = generateSprinkleTokenValue(10);

        SprinkleToken newToken = SprinkleToken.builder().value(value).build();
        sprinkleTokenRepository.save(newToken);

        return newToken;
    }

    @Override
    public SprinkleToken saveSprinkleToken(SprinkleToken token) {
        return sprinkleTokenRepository.save(token);
    }

    public String generateSprinkleTokenValue(int recursiveTry) {

        if (recursiveTry == 0)
            throw new BaseException.TokenKeyGenerationFailedException();

        int count = recursiveTry;

        String generated = StringHandleUtil.generateUriSafeRandomString(3);

        log.info("## Generated Sprinkle Token : {}, Try Remain : {}", generated, count);


        Optional<SprinkleToken> optionalSprinkleToken = sprinkleTokenRepository.findByValue(generated);

        if (optionalSprinkleToken.isEmpty())
            return generated;

        if (optionalSprinkleToken.get().getStatus().equals(SprinkleToken.Status.GENERATED))
            return generated;

        return generateSprinkleTokenValue(--count);
    }
}
