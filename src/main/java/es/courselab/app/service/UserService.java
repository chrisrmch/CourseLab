package es.courselab.app.service;

import es.courselab.app.exception.UserNotFoundException;
import es.courselab.app.model.User;
import es.courselab.app.payload.request.AccountRequestLOGIN;
import jakarta.validation.Valid;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserService {
    UserDetails loadUserByUsername(String username);

    boolean userIsActive(String email) throws UserNotFoundException;

    void saveUser(User account);
}
