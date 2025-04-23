package com.courselab.userservice.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Builder
@Table(name = "Usuario")
@Schema(description = "Entidad que representa un usuario del sistema")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único del usuario", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long idUsuario;

    @Schema(description = "Nombre completo del usuario", example = "Juan Pérez", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nombre;

    @Column(unique = true, nullable = false)
    @Schema(description = "Correo electrónico único del usuario", example = "juan@ejemplo.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @Schema(description = "Contraseña del usuario en forma hasheada", example = "$2a$10$E6nf8t...", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    @Schema(description = "Fecha de nacimiento del usuario", example = "1995-05-15", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate fechaNacimiento;

    @Schema(description = "Género del usuario", example = "Masculino")
    private String genero;

    @Schema(description = "Indica si el usuario ha confirmado su correo electrónico", example = "false")
    private Boolean emailConfirmado = false;

    @Schema(description = "Token de confirmación enviado al usuario para verificar su correo", example = "abc123token")
    private String tokenConfirmacion;
}