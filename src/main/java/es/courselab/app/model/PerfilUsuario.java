package es.courselab.app.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "perfil_usuario")
@Schema(description = "Entidad que representa el perfil extendido de un usuario")
public class PerfilUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único del perfil", example = "1")
    private Long idPerfil;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", referencedColumnName = "idUsuario")
    @JsonBackReference
    @Schema(description = "Usuario al que pertenece este perfil")
    private User usuario;

    @Schema(description = "URL del avatar del usuario", example = "https://miapp.com/imagenes/usuario1.png")
    private String fotoPerfil;

    @Schema(description = "Biografía del usuario", example = "Amante del running y las maratones")
    private String biografia;

    @Schema(description = "Sitio web personal del usuario", example = "https://miweb.com")
    private String sitioWeb;

    @Schema(description = "Localización geográfica del usuario", example = "Madrid, España")
    private String localizacion;

    @Schema(description = "Intereses del usuario", example = "running, ciclismo, trail")
    private String intereses;

    @Schema(description = "Última vez que se actualizó el perfil", example = "2025-05-06T20:30:00")
    private LocalDateTime fechaActualizacion;
}