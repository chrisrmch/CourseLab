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
@Table(name = "notificacion")
@Schema(description = "Representa una notificación que el usuario ha recibido")
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID de la notificación", example = "1")
    private Long idNotificacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    @JsonBackReference
    @Schema(description = "Usuario que recibe la notificación")
    private User usuario;

    @Schema(description = "Tipo de notificación", example = "RECORDATORIO_ENTRENAMIENTO")
    private String tipo;

    @Schema(description = "Mensaje de la notificación", example = "No olvides tu entrenamiento de hoy a las 18:00")
    private String mensaje;

    @Schema(description = "Fecha y hora en que debe mostrarse la notificación", example = "2025-05-07T10:00:00")
    private LocalDateTime fechaProgramada;

    @Schema(description = "Indica si la notificación ya ha sido leída", example = "false")
    private Boolean leida = false;
}