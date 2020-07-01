package sprinklemoney.domain.sprinkle.entity;


import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sprinklemoney.domain.user.entity.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        indexes = {
                @Index(name = "sprinkle_distribution", unique = true, columnList = "sprinkle_id,distribution_id")
        })
public class DistributionReceive {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private User receiver;

    // distribution 을 건너 관계를 맺어도 되지만 편의 + 성능상? 직접 관계를 맺어놓음
    @OneToOne
    private Sprinkle sprinkle;

    @OneToOne
    private SprinkleDistribution distribution;


    @Column(nullable = false)
    private LocalDateTime received;

    @Version
    private int version;

    @Builder
    public DistributionReceive(Sprinkle sprinkle, SprinkleDistribution distribution, User receiver) {
        this.receiver = receiver;
        this.sprinkle = sprinkle;
        this.distribution = distribution;
        this.received = LocalDateTime.now();
    }
}
