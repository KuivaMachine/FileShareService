package backend.services;

import backend.model.User;
import backend.postgres.repositories.UserRepo;

public class AuthService {
    private final UserRepo userRepository;

    public AuthService(UserRepo userRepository) {
        this.userRepository = userRepository;
    }

    public String authenticate(String username, String password) {
        User user = new User(username, password);
        return userRepository.authenticate(user);
    }


}
