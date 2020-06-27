package sprinklemoney.domain.money;

import sprinklemoney.domain.money.entity.SprinkleToken;

import java.util.Optional;

public interface SprinkleTokenService {
////
//    Optional<SprinkleToken> getUnlinkedSprinkleToken(String tokenValue, SprinkleToken.Status status);
//
//    SprinkleToken getSprinkleTokenWithGenerate(String tokenValue);

    Optional<SprinkleToken> getSprinkleToken(String tokenValue);

    SprinkleToken generateSprinkleToken();

    SprinkleToken saveSprinkleToken(SprinkleToken token);

    String generateSprinkleTokenValue(int recursiveTry);

}
