package es.courselab.app.service;

import es.courselab.app.model.AccountVerification;
import es.courselab.app.repository.AccountVerificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
@Transactional
public class AccountVerificationServiceImpl implements AccountVerificationService {

    @Autowired
    private AccountVerificationRepository accountVerificationRepository;

    @Override
    public Mono<Void> deleteAccountVerificationAndRegisterLog(AccountVerification accountVerification) {
        return accountVerificationRepository.delete(accountVerification);
    }
}