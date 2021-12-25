package works.red_eye.hood.auth.config;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import works.red_eye.hood.auth.service.RsaRegeneratorService;

import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class JwkConfig {

    private final RsaRegeneratorService rsaRegeneratorService;

    public JwkConfig(RsaRegeneratorService rsaRegeneratorService) {
        this.rsaRegeneratorService = rsaRegeneratorService;
    }

    @Bean
    public JWKSet jwkSet() {
        List<JWK> jwkList = new ArrayList<>();
        jwkList.add(getSignatureJwk());
        jwkList.add(getEncryptionJwk());

        return new JWKSet(jwkList);
    }

    private JWK getSignatureJwk() {
        KeyPair keyPair = rsaRegeneratorService.getSigningKeyPair();
        return new RSAKey.Builder((RSAPublicKey) keyPair.getPublic())
                .keyUse(KeyUse.SIGNATURE)
                .algorithm(JWSAlgorithm.RS256)
                .keyID(rsaRegeneratorService.getSigningKeyId())
                .build();
    }

    private JWK getEncryptionJwk() {
        KeyPair keyPair = rsaRegeneratorService.getEncryptionKeyPair();
        return new RSAKey.Builder((RSAPublicKey) keyPair.getPublic())
                .keyUse(KeyUse.ENCRYPTION)
                .algorithm(JWSAlgorithm.RS256)
                .keyID(rsaRegeneratorService.getEncryptionKeyId())
                .build();
    }

}