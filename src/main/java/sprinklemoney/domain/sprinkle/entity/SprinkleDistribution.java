package sprinklemoney.domain.sprinkle.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SprinkleDistribution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Sprinkle sprinkle;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDateTime created;

    @Version
    private int version;

    @Builder
    public SprinkleDistribution(Sprinkle sprinkle, BigDecimal amount) {
        this.sprinkle = sprinkle;
        this.amount = amount;
        this.created = LocalDateTime.now();
    }

}
