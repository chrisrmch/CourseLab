package es.courselab.app.service;

import es.courselab.app.exception.UserNotFoundException;
import es.courselab.app.model.User;
import es.courselab.app.repository.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with email: " + email));
    }

    @Override
    public boolean userIsActive(String email) throws UserNotFoundException {
         Optional<User> user = userRepository.findByEmail(email);
         if(user.isEmpty()) throw new UserNotFoundException();
        return user.get().getEmailConfirmado();
    }


    @Override
    public void saveUser(User account) {
        userRepository.save(account);
    }
}
