package works.red_eye.hood.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(exclude = { UserDetailsServiceAutoConfiguration.class })
public class AuthLoader {
    public static void main(String[] args) {
        SpringApplication.run(AuthLoader.class, args);
    }
}