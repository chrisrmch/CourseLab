package es.courselab.app.service;

import es.courselab.app.model.User;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserService {
    UserDetails loadUserByUsername(String username);

    void saveUser(User account);
}
