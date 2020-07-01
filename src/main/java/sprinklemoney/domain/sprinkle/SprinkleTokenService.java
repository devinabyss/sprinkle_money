package sprinklemoney.domain.sprinkle;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import sprinklemoney.common.error.BaseException;
import sprinklemoney.domain.common.util.StringHandleUtil;
import sprinklemoney.domain.sprinkle.entity.SprinkleToken;
import sprinklemoney.domain.sprinkle.repository.SprinkleTokenRepository;

import java.util.Optional;

@Slf4j
@Service
public class SprinkleTokenService {

    private final SprinkleTokenRepository sprinkleTokenRepository;

    public SprinkleTokenService(SprinkleTokenRepository sprinkleTokenRepository) {
        this.sprinkleTokenRepository = sprinkleTokenRepository;
    }

    public Optional<SprinkleToken> getSprinkleToken(String tokenValue) {
        return sprinkleTokenRepository.findByValue(tokenValue);
    }

    @Retryable(include = {DataIntegrityViolationException.class})
    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
    public SprinkleToken generateSprinkleToken() {
        String value = generateSprinkleTokenValue(10);

        SprinkleToken newToken = SprinkleToken.builder().value(value).build();
        sprinkleTokenRepository.save(newToken);

        return newToken;
    }

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

        if (optionalSprinkleToken.isEmpty() || optionalSprinkleToken.get().getStatus().equals(SprinkleToken.Status.GENERATED))
            return generated;

        return generateSprinkleTokenValue(--count);
    }
}
