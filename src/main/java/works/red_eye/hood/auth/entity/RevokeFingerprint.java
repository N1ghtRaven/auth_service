package works.red_eye.hood.auth.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "revoke_fingerprint")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RevokeFingerprint extends BaseEntity {

    @Column(name = "fingerprint")
    private String fingerprint;

}
