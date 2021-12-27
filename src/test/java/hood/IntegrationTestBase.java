package hood;

import hood.config.AbstractPostgresqlContainer;
import hood.config.AbstractRsaRegeneratorContainer;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import works.red_eye.hood.auth.AuthLoader;

@SpringBootTest(classes = AuthLoader.class)
@ContextConfiguration(initializers = {
       AbstractPostgresqlContainer.Initializer.class,
       AbstractRsaRegeneratorContainer.Initializer.class
})
public abstract class IntegrationTestBase {
    @BeforeAll
    static void init() {
        AbstractPostgresqlContainer.container.start();
        AbstractRsaRegeneratorContainer.container.start();
    }
}
