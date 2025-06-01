package es.courselab.app.repository;

import es.courselab.app.model.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Repository
public interface UserRepository extends ReactiveCrudRepository<User, Integer>, ReactiveSortingRepository<User, Integer> {
    Mono<User> findByEmail(String email);
    Mono<Boolean> existsByEmail(String email);
}
