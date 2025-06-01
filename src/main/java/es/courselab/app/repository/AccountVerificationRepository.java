package es.courselab.app.repository;

import es.courselab.app.model.AccountVerification;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.security.core.Transient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import javax.swing.text.StyledEditorKit;
import java.util.Optional;

@Repository
public interface AccountVerificationRepository extends ReactiveCrudRepository<AccountVerification, String>, ReactiveSortingRepository<AccountVerification, String> {
    Mono<AccountVerification> findByEmail(String email);
    Mono<Boolean> existsByEmail(String email);
}
