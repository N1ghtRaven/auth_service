package works.red_eye.hood.auth.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import works.red_eye.hood.auth.entity.User;
import works.red_eye.hood.auth.exception.NotFoundException;
import works.red_eye.hood.auth.repository.UserRepository;
import works.red_eye.hood.auth.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepo;

    public UserServiceImpl(PasswordEncoder passwordEncoder, UserRepository userRepo) {
        this.passwordEncoder = passwordEncoder;
        this.userRepo = userRepo;
    }

    @Override
    public User getUser(String username) throws NotFoundException {
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User", username));
    }

    @Override
    public boolean isCorrectPassword(User user, String password) {
        return passwordEncoder.matches(password, user.getPassword());
    }
}