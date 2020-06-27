package sprinklemoney;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;

@Slf4j
public class JustTest {
    private SecureRandom random = new SecureRandom();

    @Test
    public void test(){
        for (int i = 0; i < 10; i++){
            BigDecimal percent = BigDecimal.valueOf(random.nextInt(99)).multiply(BigDecimal.valueOf(0.01));
            log.info("## percent : {}", percent);
        }

    }
}
