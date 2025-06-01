package es.courselab.app.model;

import es.courselab.app.enumerated.EAccountRole;
import es.courselab.app.enumerated.EAccountState;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.boot.r2dbc.ConnectionFactoryBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.ConnectionBuilder;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;

@Table("usuarios")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Schema(description = "Entidad que representa un usuario del sistema")
public class User implements UserDetails {

    @Id
    @Column("id_usuario")
    @Schema(description = "Identificador único del usuario", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long usuarioID;

    @Column("id_perfil")
    @Schema(description = "ID del perfil extendido del usuario", example = "5")
    private Long perfilID;

    @Column("nombre")
    @Schema(description = "Nombre del usuario", example = "Juan", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nombre;

    @Column("apellidos")
    @Schema(description = "Apellidos del usuario", example = "Rodriguez", requiredMode = Schema.RequiredMode.REQUIRED)
    private String apellidos;

    @Column("email")
    @Schema(description = "Correo electrónico único del usuario", example = "juan@ejemplo.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @Column("password")
    @Schema(description = "Contraseña del usuario en forma hasheada", example = "$2a$10$E6nf8t...", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    @Column("fecha_nacimiento")
    @Schema(description = "Fecha de nacimiento del usuario", example = "1995-05-15T00:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime fechaNacimiento;

    @Column("fecha_creacion")
    @Schema(description = "Fecha de creación de la cuenta", example = "2025-05-01T12:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime fechaCreacion;

    @Column("genero")
    @Schema(description = "Género del usuario", example = "Masculino")
    private String genero;

    @Column("role")
    @Schema(description = "Privilegios/tipo de cuenta", example = "ROLE_USER")
    private EAccountRole role;

    @Column("email_confirmado")
    @Schema(description = "Indica si el usuario ha confirmado su correo electrónico", example = "false")
    private Boolean emailConfirmado = false;

    @Column("token_confirmacion")
    @Schema(description = "Token de confirmación enviado al usuario para verificar su correo", example = "abc123token")
    private String tokenConfirmacion;

    @Column("estado")
    @Schema(description = "Estado de la cuenta", example = "ACTIVE")
    private EAccountState estado;

    // En entorno reactivo, las relaciones se gestionan en el servicio/repository, no en la entidad.
    // Para referenciar entrenamientos y notificaciones, se usarán consultas adhoc en el repositorio reactivo.

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        // Se pueden implementar reglas adicionales si se desea
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // Se pueden implementar reglas adicionales si se desea
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // Se pueden implementar reglas adicionales si se desea
        return true;
    }

    @Override
    public boolean isEnabled() {
        return estado == EAccountState.ACTIVE && Boolean.TRUE.equals(emailConfirmado);
    }
}
