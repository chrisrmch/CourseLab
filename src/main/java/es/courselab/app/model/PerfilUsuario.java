package es.courselab.app.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.List;

@Table("perfil_usuario")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Entidad que representa el perfil extendido de un usuario")
public class PerfilUsuario {

    @Id
    @Column("id_perfil")
    @Schema(description = "ID único del perfil", example = "1")
    private Long perfilID;

    @Column("id_usuario")
    @Schema(description = "ID del usuario al que pertenece este perfil", example = "42", required = true)
    private Long usuarioID;

    @Column("foto_perfil")
    @Schema(description = "URL del avatar del usuario", example = "https://miapp.com/imagenes/usuario1.png")
    private String fotoPerfil;

    @Column("descripcion")
    @Schema(description = "Biografía del usuario", example = "Amante del running y las maratones")
    private String descripcion;

    @Column("sitio_web")
    @Schema(description = "Sitio web personal del usuario", example = "https://miweb.com")
    private String sitioWeb;

    @Column("localizacion")
    @Schema(description = "Localización geográfica del usuario", example = "Madrid, España")
    private String localizacion;

    /**
     * Suponiendo que en la base de datos esta columna está definida como TEXT[] en PostgreSQL,
     * Spring Data R2DBC la mapea automáticamente a List<String>.
     */
    @Column("intereses")
    @Schema(description = "Intereses del usuario", example = "[\"running\", \"ciclismo\", \"trail\"]")
    private List<String> intereses;

    @Column("fecha_actualizacion")
    @Schema(description = "Última vez que se actualizó el perfil", example = "2025-05-06T20:30:00")
    private LocalDateTime fechaActualizacion;
}
