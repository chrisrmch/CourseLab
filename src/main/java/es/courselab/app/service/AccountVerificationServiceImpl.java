package es.courselab.app.service;

import es.courselab.app.model.AccountVerification;
import es.courselab.app.repository.AccountVerificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountVerificationServiceImpl implements AccountVerificationService {

    @Autowired
    private AccountVerificationRepository accountVerificationRepository;

    @Override
    public void deleteAccountVerificationAndRegisterLog(AccountVerification accountVerification) {
        accountVerificationRepository.delete(accountVerification);
    }
}