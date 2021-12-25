package works.red_eye.hood.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthRequest {
    private final String username;
    private final String password;
}