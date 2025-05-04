package es.courselab.app.repository;

import es.courselab.app.model.AccountVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountVerificationRepository extends JpaRepository<AccountVerification, Long> {
    Optional<AccountVerification> findByEmail(String email);
}
