package es.courselab.app.payload.request;

import lombok.Data;

@Data
public class AccountRequestLOGIN {
    private String email;
    private String password;
}
