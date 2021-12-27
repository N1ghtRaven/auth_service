package hood.config;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.GenericContainer;

@UtilityClass
public class AbstractRsaRegeneratorContainer {
    public static final GenericContainer<?> container
            = new GenericContainer<>(getImageName()).withEnv("REGEN_SALT", "some_salt").withExposedPorts(8001);

    private static String getImageName() {
        String imageName = System.getenv().get("REGENERATOR_IMAGE");
        return imageName != null ? imageName : "hood/rsa_regenerator:1.0.0";
    }

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        private final String URL = String.format("http://%s:%d/", container.getContainerIpAddress(), container.getMappedPort(8001));
        private final static String SECRET_SALT = "some_secret_salt";
        private final static String SIGNING_SECRET = "some_singing_secret";
        private final static String ENCRYPTION_SECRET = "some_encryption_secret";

        @Override
        public void initialize(@NotNull ConfigurableApplicationContext applicationContext) {
            TestPropertyValues.of(
                    "jwt.secret.salt=" + SECRET_SALT,
                    "jwt.signing.secret=" + SIGNING_SECRET,
                    "jwt.encryption.secret=" + ENCRYPTION_SECRET,
                    "jwt.rsa_regenerator.url=" + URL,
                    "jwt.access_token.expiration=" + "2",
                    "jwt.refresh_token.expiration=" + "2",
                    "fingerprint.clean.enabled=" + "false",
                    "fingerprint.clean.interval=" + "PT30S"
            ).applyTo(applicationContext);
        }
    }
}
