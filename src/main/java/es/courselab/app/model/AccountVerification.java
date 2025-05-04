package es.courselab.app.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@JsonIgnoreProperties("hibernateLazyInitializer")
@Table(name = "account_verification")
public class AccountVerification {
    @Id
    @Schema(description = "Identificador del token de activación de cuenta.", example = "1")
    private String id;

    @Schema(description = "Email de envío del token de activación.", example = "1")
    private String email;
}