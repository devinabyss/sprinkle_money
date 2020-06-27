package sprinklemoney.domain.money;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import sprinklemoney.domain.common.util.StringHandleUtil;
import sprinklemoney.domain.money.entity.SprinkleToken;
import sprinklemoney.domain.money.repository.SprinkleTokenRepository;

import java.util.Optional;

@Service
public class SprinkleTokenServiceImpl implements SprinkleTokenService {

    private final SprinkleTokenRepository sprinkleTokenRepository;

    public SprinkleTokenServiceImpl(SprinkleTokenRepository sprinkleTokenRepository) {
        this.sprinkleTokenRepository = sprinkleTokenRepository;
    }

//    @Override
//    public Optional<SprinkleToken> getUnlinkedSprinkleToken(String tokenValue, SprinkleToken.Status status) {
//        return sprinkleTokenRepository.findByValueAndStatus(tokenValue, status);
//    }
//
//    @Override
//    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.SERIALIZABLE)
//    public SprinkleToken getSprinkleTokenWithGenerate(String tokenValue) {
//
//
//
//
//        return getUnlinkedSprinkleToken(tokenValue, SprinkleToken.Status.GENERATED).orElseGet(this::generateSprinkleToken);
//    }

    @Override
    public Optional<SprinkleToken> getSprinkleToken(String tokenValue) {
        return sprinkleTokenRepository.findByValue(tokenValue);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.SERIALIZABLE)
    public SprinkleToken generateSprinkleToken() {
        String value = generateSprinkleTokenValue();

        SprinkleToken newToken = SprinkleToken.builder().value(value).build();
        sprinkleTokenRepository.save(newToken);

        return newToken;
    }

    @Override
    public SprinkleToken saveSprinkleToken(SprinkleToken token) {
        return sprinkleTokenRepository.save(token);
    }

    private String generateSprinkleTokenValue() {
        return StringHandleUtil.generateUriSafeRandomString(3);
    }
}
