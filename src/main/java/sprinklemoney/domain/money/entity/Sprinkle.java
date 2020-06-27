package sprinklemoney.domain.money.entity;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import sprinklemoney.common.error.BaseException;
import sprinklemoney.common.error.ErrorStatus;
import sprinklemoney.domain.user.entity.User;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Getter
@Entity
@Table(
        indexes = {
                @Index(name = "token", unique = true, columnList = "token_id"),
                @Index(name = "author", unique = false, columnList = "author_id")
        })
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Sprinkle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    private SprinkleToken token;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "sprinkle_id")
    private List<SprinkleReceive> sprinkleReceives;

    @Column
    private BigDecimal sprinkleAmount;

    @Column
    private int divideSize;

    @OneToOne
    private User author;

    @Column
    private String roomId;

    @Column
    private LocalDateTime created;

    @Builder
    public Sprinkle(User author, SprinkleToken token, BigDecimal sprinkleAmount, int divideSize, String roomId) {
        this.author = author;
        this.token = token;
        this.sprinkleAmount = sprinkleAmount;
        this.divideSize = divideSize;
        this.roomId = roomId;
        this.created = LocalDateTime.now();
    }

    public boolean isGivableStatus() {
        log.info("## Sprinkle : {}", this);

        log.info("## Receivies : {}", getSprinkleReceives());
        boolean isBefore = created.isBefore(LocalDateTime.now().minus(10, ChronoUnit.MINUTES));
        log.info("## isBefore : {}", isBefore);
        if (isBefore)
            return false;

        boolean isFullSize = getSprinkleReceives().size() >= divideSize;
        log.info("## isFullSize : {}", isFullSize);
        if (isFullSize)
            return false;



        boolean isFullAmount = getSprinkleReceives().stream().map(SprinkleReceive::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add).compareTo(sprinkleAmount) > -1;
        log.info("## isFullAmount : {}", isFullAmount);
        if (isFullAmount)
            return false;

        return true;
    }

    public SprinkleReceive share(User receiver, SecureRandom random, String roomId) {
        if (getSprinkleReceives().stream().anyMatch(receive -> receive.getReceiver().equals(receiver)))
            throw new BaseException(ErrorStatus.ALREADY_RECEIVED_SPRINKLE);

        if (!this.roomId.equals(roomId))
            throw new BaseException(ErrorStatus.NOT_ELIGIBLE);

        if (!isGivableStatus())
            throw new BaseException(ErrorStatus.INVALID_SPRINKLE_STATUS);


        int currentDivideSize = divideSize - getSprinkleReceives().size();
        BigDecimal restAmount = sprinkleAmount.subtract(getSprinkleReceives().stream().map(SprinkleReceive::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add));

        SprinkleReceive sprinkleReceive = SprinkleReceive.builder()
                .amount(decisionShareValue(currentDivideSize, restAmount, random))
                .receiver(receiver)
                .sprinkle(this)
                .build();

        return sprinkleReceive;
    }

    private BigDecimal decisionShareValue(int restShareSize, BigDecimal restAmount, SecureRandom random) {
        if (restShareSize == 1)
            return restAmount;

        BigDecimal percent = BigDecimal.valueOf(random.nextInt(99)).multiply(BigDecimal.valueOf(0.01));

        return restAmount.multiply(percent).setScale(0, RoundingMode.HALF_UP);
    }

}
