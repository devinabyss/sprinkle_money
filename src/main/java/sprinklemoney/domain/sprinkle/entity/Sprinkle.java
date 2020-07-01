package sprinklemoney.domain.sprinkle.entity;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import sprinklemoney.common.error.BaseException;
import sprinklemoney.common.error.ErrorStatus;
import sprinklemoney.domain.sprinkle.SprinkleDistributionService;
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
@ToString(exclude = "receives")
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
    private ChronoUnit timeLimitUnit;

    @Column(nullable = false)
    private Long timeLimitValue;

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

    /**
     * @param target 뿌리기 생성 후 분배 제한 설정 시간
     */
    public boolean isCreatedAfter(LocalDateTime target) {
        return this.created.isAfter(target);
    }

    public ReceivableStatus getReceivableTarget(SprinkleDistributionService distributionService, User receiveTarget, String requestedRoomId) {
        if (receiveTarget.equals(author) || !requestedRoomId.equals(getRoomId()))
            return ReceivableStatus.NOT_QUALIFIED;

        if (distributionService.findReceivedHistory(this, receiveTarget).isPresent())
            return ReceivableStatus.ALREADY_RECEIVED;

        return ReceivableStatus.RECEIVABLE;
    }


    public DistributionReceive distributeToReceiver(SprinkleDistributionService service, User receiver, String requestedRoomId) {

        if (!isCreatedAfter(LocalDateTime.now().minus(this.timeLimitValue, this.timeLimitUnit)))
            throw new BaseException(ErrorStatus.INVALID_SPRINKLE_STATUS);


        ReceivableStatus receivableStatus = getReceivableTarget(service, receiver, requestedRoomId);
        if (!receivableStatus.isReceivable)
            throw new BaseException(receivableStatus.errorStatus);

        /** 분배 기록 객체만 생성하는 것이 객체의 role 일까, 저장까지 스스로 하는 것이 객체의 role 일까. 우선 저장은 서비스에 일임하는 것으로 통일.. */
        SprinkleDistribution targetDistribution = service.getAvailableDistribution(this).orElseThrow(() -> new BaseException(ErrorStatus.INVALID_SPRINKLE_STATUS));
        return DistributionReceive.builder().sprinkle(this).distribution(targetDistribution).receiver(receiver).build();
        //service.saveDistributionReceive(DistributionReceive.builder().sprinkle(this).distribution(targetDistribution).receiver(receiver).build());
    }

    public List<SprinkleDistribution> generateDistribution(SecureRandom random) {
        AtomicReference<BigDecimal> reference = new AtomicReference<>(sprinkleAmount);

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


    public enum ReceivableStatus {
        RECEIVABLE(true, null),
        NOT_QUALIFIED(false, ErrorStatus.NOT_ELIGIBLE),
        ALREADY_RECEIVED(false, ErrorStatus.ALREADY_RECEIVED_SPRINKLE);

        private final Boolean isReceivable;
        private final ErrorStatus errorStatus;

        ReceivableStatus(boolean isReceivable, ErrorStatus errorStatus) {
            this.isReceivable = isReceivable;
            this.errorStatus = errorStatus;
        }
    }
}
