package sprinklemoney.api.dto;

import com.sun.istack.NotNull;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import java.math.BigDecimal;

@Data
public class CreateSprinkleRequest {

    @NotNull
    @Min(value = 1)
    private Integer divideSize;

    @NotNull
    @DecimalMin(value = "1.0")
    private BigDecimal amount;

}
