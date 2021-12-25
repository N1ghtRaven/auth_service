package works.red_eye.hood.auth.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import works.red_eye.hood.auth.entity.Fingerprint;
import works.red_eye.hood.auth.repository.FingerprintRepository;
import works.red_eye.hood.auth.service.FingerprintService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Service
public class FingerprintServiceImpl implements FingerprintService {

    private final FingerprintRepository revokeFingerprintRepo;

    @Value("${jwt.refresh_token.expiration}")
    private Integer expiration;

    public FingerprintServiceImpl(FingerprintRepository revokeFingerprintRepo) {
        this.revokeFingerprintRepo = revokeFingerprintRepo;
    }

    @Override
    public void revoke(String fingerprint) {
        Fingerprint fgp = new Fingerprint();
        fgp.setFingerprint(fingerprint);
        fgp.setExpire(LocalDateTime.now().plusSeconds(expiration));

        revokeFingerprintRepo.save(fgp);
    }

    @Override
    public boolean isRevoked(String fingerprint) {
        return revokeFingerprintRepo.existsByFingerprint(fingerprint);
    }

    @Override
    @Scheduled(fixedDelayString = "${fingerprint.clean.interval}")
    @Transactional
    public void cleanExpired() {
         revokeFingerprintRepo.deleteAllByExpireBefore(LocalDateTime.now());
    }
}