package works.red_eye.hood.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import works.red_eye.hood.auth.entity.Fingerprint;

import java.time.LocalDateTime;

public interface FingerprintRepository extends JpaRepository<Fingerprint, Long> {
    void deleteAllByExpireBefore(LocalDateTime expiredDate);
    boolean existsByFingerprint(String fingerprint);
}