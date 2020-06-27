package sprinklemoney.domain.money.entity;

import lombok.*;

import javax.persistence.*;

@Getter
@Entity
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SprinkleToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String value;

    @Setter
    @Column
    private Status status;

    @Builder
    public SprinkleToken(String value) {
        this.value = value;
        this.status = Status.GENERATED;
    }

    public enum Status {
        GENERATED, LINKED
    }
}
