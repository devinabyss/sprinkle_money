package sprinklemoney.domain.money;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import sprinklemoney.domain.common.util.StringHandleUtil;
import sprinklemoney.domain.money.entity.SprinkleToken;
import sprinklemoney.domain.money.repository.SprinkleTokenRepository;

import java.util.Optional;

@Slf4j
@RunWith(MockitoJUnitRunner.class)
public class SprinkleTokenServiceImplUnitTest {

    private SprinkleTokenRepository sprinkleTokenRepository;

    private SprinkleTokenServiceImpl service;

    @Before
    public void init() {
        sprinkleTokenRepository = Mockito.mock(SprinkleTokenRepository.class);
        service = Mockito.spy(new SprinkleTokenServiceImpl(sprinkleTokenRepository));
    }

    @Test
    public void testGetSprinkleToken() {
        String tokenValue = StringHandleUtil.generateUriSafeRandomString(5);

        Mockito.when(sprinkleTokenRepository.findByValue(tokenValue)).thenReturn(Optional.of(SprinkleToken.builder().value(tokenValue).build()));

        SprinkleToken token = service.getSprinkleToken(tokenValue).orElseThrow(RuntimeException::new);

        log.info("## Sprinkle Token : {}", token);

        Assert.assertEquals(token.getValue(), tokenValue);
    }

    @Test
    public void testGetSprinkleTokenWithGenerate() {
        SprinkleToken token = service.generateSprinkleToken();

        log.info("## Sprinkle Token : {}", token);

        Assert.assertEquals(3, token.getValue().length());
        Mockito.verify(service, Mockito.atLeastOnce()).generateSprinkleTokenValue(10);
    }
}
