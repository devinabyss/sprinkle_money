package sprinklemoney.domain.sprinkle.entity;

import lombok.*;
import sprinklemoney.domain.user.entity.User;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Entity
@ToString
@Table(
        indexes = {
                @Index(name = "sprinkle", unique = true, columnList = "sprinkle_id,receiver_id")
                //@Index(name = "receiver", unique = true, columnList = "receiver_id,sprinkle_id")
        })
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SprinkleReceive {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @OneToOne
    private User receiver;

    @OneToOne(fetch = FetchType.EAGER)
    private Sprinkle sprinkle;

    @Column
    private BigDecimal amount;

    @Column
    private LocalDateTime created;

    @Version
    @Column
    private int version;

    @Builder
    public SprinkleReceive(User receiver, Sprinkle sprinkle, BigDecimal amount) {
        this.receiver = receiver;
        this.sprinkle = sprinkle;
        this.amount = amount;
        this.created = LocalDateTime.now();
    }

}
