package es.courselab.app.service;

import es.courselab.app.model.AccountVerification;

public interface AccountVerificationService {
    void deleteAccountVerificationAndRegisterLog(AccountVerification accountVerification);
}
