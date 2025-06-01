package es.courselab.app.service;

import es.courselab.app.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import reactor.core.publisher.Mono;

public interface UserService {
    Mono<UserDetails> loadUserByUsername(String username);

    Mono<User> saveUser(User account);
}
