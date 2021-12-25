package works.red_eye.hood.auth.service;

import works.red_eye.hood.auth.entity.User;
import works.red_eye.hood.auth.exception.NotFoundException;

public interface UserService {
    User getUser(String username) throws NotFoundException;
    boolean isCorrectPassword(User user, String password);
}