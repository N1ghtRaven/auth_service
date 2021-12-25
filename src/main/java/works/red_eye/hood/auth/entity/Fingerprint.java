package works.red_eye.hood.auth.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "fingerprint")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Fingerprint extends BaseEntity {

    @Column(name = "fingerprint")
    private String fingerprint;

    @Column(name = "expire")
    private LocalDateTime expire;

}
