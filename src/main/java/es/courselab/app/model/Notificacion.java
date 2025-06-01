package es.courselab.app.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("notificacion")
@Schema(description = "Representa una notificación que el usuario ha recibido")
public class Notificacion {

    @Id
    @Column("id_notificacion")
    @Schema(description = "ID de la notificación", example = "1")
    private Long idNotificacion;

    @Column("id_usuario")
    @Schema(description = "ID del usuario que recibe la notificación", example = "42")
    private Long usuarioId;

    @Schema(description = "Tipo de notificación", example = "RECORDATORIO_ENTRENAMIENTO")
    private String tipo;

    @Schema(description = "Mensaje de la notificación", example = "No olvides tu entrenamiento de hoy a las 18:00")
    private String mensaje;

    @Column("fecha_programada")
    @Schema(description = "Fecha y hora en que debe mostrarse la notificación", example = "2025-05-07T10:00:00")
    private LocalDateTime fechaProgramada;

    @Schema(description = "Indica si la notificación ya ha sido leída", example = "false")
    private Boolean leida = false;
}
