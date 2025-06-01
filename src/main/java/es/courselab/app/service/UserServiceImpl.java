package es.courselab.app.service;
import es.courselab.app.repository.UserRepository;
import es.courselab.app.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
@Transactional
public class UserServiceImpl implements ReactiveUserDetailsService {
    @Autowired
    private UserRepository userRepository;

    public Mono<User> saveUser(User account) {
        return userRepository.save(account);
    }

    @Override
    public Mono<UserDetails> findByUsername(String email) {
        return userRepository.findByEmail(email)
                .switchIfEmpty(Mono.error(
                        new UsernameNotFoundException("User Not Found with email: " + email)))
                .map(domainUser -> org.springframework.security.core.userdetails.User.withUsername(domainUser.getEmail())
                        .password(domainUser.getPassword())
                        .roles(domainUser.getRole().name().replace("ROLE_", ""))
                        .build()
                );
    }
}
