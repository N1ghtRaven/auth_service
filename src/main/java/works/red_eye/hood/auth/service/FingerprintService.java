package works.red_eye.hood.auth.service;

public interface FingerprintService {
    void revoke(String fingerprint);
    boolean isRevoked(String fingerprint);
}