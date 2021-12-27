package works.red_eye.hood.auth.service.impl;

import org.apache.commons.codec.digest.DigestUtils;
import org.bouncycastle.asn1.*;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.postgresql.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import works.red_eye.hood.auth.service.RsaRegeneratorService;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.*;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class RsaRegeneratorServiceImpl implements RsaRegeneratorService {
    @Value("${jwt.rsa_regenerator.url}")
    private String url;

    @Value("${jwt.signing.secret}")
    private String signingSecret;

    @Value("${jwt.encryption.secret}")
    private String encryptionSecret;

    @Value("${jwt.secret.salt}")
    private String secretSalt;

    private String signingId;
    private KeyPair signingPair;

    private String encryptionId;
    private KeyPair encryptionPair;

    @PostConstruct
    public void init() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        this.signingId = DigestUtils.sha256Hex(signingSecret + "_" + secretSalt);
        this.signingPair = receiveKeyPair(signingSecret);

        this.encryptionId = DigestUtils.sha256Hex(encryptionId + "_" + secretSalt);
        this.encryptionPair = receiveKeyPair(encryptionSecret);
    }

    @Override
    public String getSigningKeyId() {
        return this.signingId;
    }

    @Override
    public KeyPair getSigningKeyPair() {
        return this.signingPair;
    }

    @Override
    public String getEncryptionKeyId() {
        return this.encryptionId;
    }

    @Override
    public KeyPair getEncryptionKeyPair() {
        return this.encryptionPair;
    }

    private KeyPair receiveKeyPair(String secret) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        JSONObject rsaResponse = getRsaResponse(secret);
        byte[] data = Base64.decode(rsaResponse.getString("data"));
        byte[] keyData = convertKeyFormat(data);
        return getKeyPair(keyData);
    }

    private KeyPair getKeyPair(byte[] keyData) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PrivateKey privateKey = getPrivateKey(keyData);
        PublicKey publicKey = getPublicKey(privateKey);

        return new KeyPair(publicKey, privateKey);
    }

    private PublicKey getPublicKey(PrivateKey privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        RSAPrivateKeySpec privateKeySpec = keyFactory.getKeySpec(privateKey, RSAPrivateKeySpec.class);
        RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(privateKeySpec.getModulus(), BigInteger.valueOf(65537));

        return keyFactory.generatePublic(publicKeySpec);
    }

    private PrivateKey getPrivateKey(byte[] keyData) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(keyData));
    }

    private byte[] convertKeyFormat(byte[] data) throws IOException {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(new ASN1Integer(0));
        ASN1EncodableVector v2 = new ASN1EncodableVector();
        v2.add(new ASN1ObjectIdentifier(PKCSObjectIdentifiers.rsaEncryption.getId()));
        v2.add(DERNull.INSTANCE);
        v.add(new DERSequence(v2));
        v.add(new DEROctetString(data));
        ASN1Sequence seq = new DERSequence(v);
        return seq.getEncoded("DER");
    }

    private JSONObject getRsaResponse(String secret) throws IOException {
        String response = makeRequest(secret);
        assert response != null;
        return new JSONObject(response);
    }

    private String makeRequest(String secret) throws IOException {
        Request request = new Request.Builder().url(url + secret).method("GET", null).build();
        Response response = getHttpClient().newCall(request).execute();
        if (response.code() == 200)
            return Objects.requireNonNull(response.body()).string();

        return null;
    }

    private OkHttpClient getHttpClient() {
        return new OkHttpClient()
                .newBuilder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }

}