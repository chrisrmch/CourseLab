package es.courselab.app.payload.request;

import es.courselab.app.enumerated.EAccountRole;
import lombok.Data;

@Data
public class AccountRequestPOST {
    private String email;
    private String password;
    private EAccountRole role;
}
