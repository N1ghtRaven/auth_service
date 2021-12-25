package works.red_eye.hood.auth.service;

import java.security.KeyPair;

public interface RsaRegeneratorService {
    String getSigningKeyId();
    KeyPair getSigningKeyPair();

    String getEncryptionKeyId();
    KeyPair getEncryptionKeyPair();
}