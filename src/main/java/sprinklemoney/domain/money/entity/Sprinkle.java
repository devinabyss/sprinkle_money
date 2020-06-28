package sprinklemoney.domain.money.entity;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import sprinklemoney.common.error.BaseException;
import sprinklemoney.common.error.ErrorStatus;
import sprinklemoney.domain.money.SprinkleDistributionService;
import sprinklemoney.domain.user.entity.User;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Getter
@Entity
@Table(
        indexes = {
                @Index(name = "token", unique = true, columnList = "token_id"),
                @Index(name = "author", unique = false, columnList = "author_id")
        })
@ToString(exclude = "sprinkleReceives")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Sprinkle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER, optional = false)
    private SprinkleToken token;

    @Setter
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "sprinkle_id")
    @Deprecated
    private List<SprinkleReceive> sprinkleReceives;


    @Setter
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "sprinkle_id")
    private List<DistributionReceive> receives;

    @Column(nullable = false)
    private BigDecimal sprinkleAmount;


    @Column(nullable = false)
    private int divideSize;

    @OneToOne
    private User author;

    @Column(nullable = false)
    private String roomId;

    @Column(nullable = false)
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

    public void checkAcceptableConditionToDistribute(SprinkleDistributionService distributionService, User receiver, String requestedRoomId) {
        if (this.created.isBefore(LocalDateTime.now().minus(10, ChronoUnit.MINUTES)))
            throw new BaseException(ErrorStatus.INVALID_SPRINKLE_STATUS);

        if (receiver.equals(author) || !requestedRoomId.equals(getRoomId()))
            throw new BaseException(ErrorStatus.NOT_ELIGIBLE);

        if (distributionService.findReceivedHistory(this, receiver).isPresent())
            throw new BaseException(ErrorStatus.ALREADY_RECEIVED_SPRINKLE);
    }

    public DistributionReceive distributeToReceiver(SprinkleDistributionService service, User receiver, String requestedRoomId) {
        checkAcceptableConditionToDistribute(service, receiver, requestedRoomId);

        SprinkleDistribution targetDistribution = service.getAvailableDistribution(this).orElseThrow(() -> new BaseException(ErrorStatus.INVALID_SPRINKLE_STATUS));

        return service.saveDistributionReceive(DistributionReceive.builder().sprinkle(this).distribution(targetDistribution).receiver(receiver).build());
    }







    public SprinkleReceive shareLogicWhenReceiveRequested(User receiver, SecureRandom random, String roomId) {

        if (created.isBefore(LocalDateTime.now().minus(10, ChronoUnit.MINUTES)))
            throw new BaseException(ErrorStatus.INVALID_SPRINKLE_STATUS);

        if (!this.roomId.equals(roomId))
            throw new BaseException(ErrorStatus.NOT_ELIGIBLE);


        AtomicReference<BigDecimal> reference = new AtomicReference<>(BigDecimal.ZERO);

        getSprinkleReceives().forEach(receive -> {
                    reference.set(reference.get().add(receive.getAmount()));
                    if (receive.getReceiver().equals(receiver))
                        throw new BaseException(ErrorStatus.ALREADY_RECEIVED_SPRINKLE);
                }
        );

        if (reference.get().compareTo(sprinkleAmount) > -1)
            throw new BaseException(ErrorStatus.INVALID_SPRINKLE_STATUS);


        int currentDivideSize = divideSize - getSprinkleReceives().size();
        BigDecimal restAmount = sprinkleAmount.subtract(reference.get());

        SprinkleReceive sprinkleReceive = SprinkleReceive.builder()
                .amount(decisionShareValue(currentDivideSize, restAmount, random))
                .receiver(receiver)
                .sprinkle(this)
                .build();

        return sprinkleReceive;
    }

    public List<SprinkleReceive> shareLogicWhenSprinkleCreated(SecureRandom random) {

        AtomicReference<BigDecimal> reference = new AtomicReference<BigDecimal>(sprinkleAmount);

        List<SprinkleReceive> receives = new ArrayList<>();

        for (int i = divideSize; i > 0; i--) {
            BigDecimal currentShare = decisionShareValue(i, reference.get(), random);
            reference.set(reference.get().subtract(currentShare));
            receives.add(SprinkleReceive.builder().sprinkle(this).amount(currentShare).build());
        }

        return receives;
    }

    public List<SprinkleDistribution> distributeWhenSprinkleCreated(SecureRandom random) {
        AtomicReference<BigDecimal> reference = new AtomicReference<BigDecimal>(sprinkleAmount);

        List<SprinkleDistribution> distributions = new ArrayList<>();

        for (int i = divideSize; i > 0; i--) {
            BigDecimal currentShare = decisionShareValue(i, reference.get(), random);
            reference.set(reference.get().subtract(currentShare));
            distributions.add(SprinkleDistribution.builder().sprinkle(this).amount(currentShare).build());
        }

        return distributions;

    }

    private BigDecimal decisionShareValue(int restShareSize, BigDecimal restAmount, SecureRandom random) {
        if (restShareSize == 1)
            return restAmount;

        BigDecimal percent = BigDecimal.valueOf(random.nextInt(99)).multiply(BigDecimal.valueOf(0.01));

        return restAmount.multiply(percent).setScale(0, RoundingMode.HALF_UP);
    }


}
