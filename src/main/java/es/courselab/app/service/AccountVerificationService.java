package es.courselab.app.service;

import es.courselab.app.model.AccountVerification;
import reactor.core.publisher.Mono;


public interface AccountVerificationService {
    Mono<Void> deleteAccountVerificationAndRegisterLog(AccountVerification accountVerification);
}
