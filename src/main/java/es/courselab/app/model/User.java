package es.courselab.app.model;

import es.courselab.app.enumerated.EAccountRole;
import es.courselab.app.enumerated.EAccountState;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Builder
@Table(name = "User")
@Schema(description = "Entidad que representa un usuario del sistema")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único del usuario", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long idUsuario;

    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Schema(description = "Perfil extendido del usuario")
    private PerfilUsuario perfil;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Schema(description = "Entrenamientos realizados por el usuario")
    private List<Actividad> entrenamientos;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Schema(description = "Notificaciones recibidas por el usuario")
    private List<Notificacion> notificaciones;

    @Schema(description = "Nombre del usuario", example = "Juan", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nombre;

    @Schema(description = "Apellidos del usuario", example = "Rodriguez", requiredMode = Schema.RequiredMode.REQUIRED)
    private String apellidos;

    @Column(unique = true, nullable = false)
    @Schema(description = "Correo electrónico único del usuario", example = "juan@ejemplo.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @Schema(description = "Contraseña del usuario en forma hasheada", example = "$2a$10$E6nf8t...", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    @Schema(description = "Fecha de nacimiento del usuario", example = "1995-05-15", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime fechaNacimiento;

    @Schema(description = "Fecha de nacimiento del usuario", example = "1995-05-15", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime fechaCrecion;

    @Schema(description = "Género del usuario", example = "Masculino")
    private String genero;

    @Schema(description = "Privilegios/tipo de cuenta.", example = "ROLE_USER, ROLE_ADMIN")
    @Enumerated(value = EnumType.STRING)
    private EAccountRole role;

    @Schema(description = "Indica si el usuario ha confirmado su correo electrónico", example = "false")
    private Boolean emailConfirmado = false;

    @Schema(description = "Token de confirmación enviado al usuario para verificar su correo", example = "abc123token")
    private String tokenConfirmacion;

    @Schema(description = "Estado de la cuenta.", example = "INACTIVE, ACTIVE")
    @Enumerated(value = EnumType.STRING)
    private EAccountState estado;

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
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}