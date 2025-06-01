package es.courselab.app.model;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;


@Table(name = "account_verification")
public class AccountVerification implements Persistable<String> {
    @Id
    @Schema(description = "Identificador del token de activación de cuenta.", example = "1")
    @Setter
    private String id;

    @Schema(description = "Email de envío del token de activación.", example = "1")
    @Setter
    @Getter
    private String email;

    @Transient
    private boolean isNew = true;

    @Override
    public boolean isNew() {
        return this.isNew;
    }

    @Override
    public String getId() {
        return this.id;
    }

    public void setAsAccountVerificationExists() {
        this.isNew = false;
    }
}