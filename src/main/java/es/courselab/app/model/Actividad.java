package es.courselab.app.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("entrenamiento")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Representa una sesión de entrenamiento realizada por el usuario")
public class Actividad {

    @Id
    @Column("id_actividad")
    @Schema(description = "ID del entrenamiento", example = "1")
    private Long idActividad;

    @Column("id_usuario")
    @Schema(description = "ID del usuario que realizó el entrenamiento", example = "42")
    private Long usuarioId;

    @Schema(description = "Fecha del entrenamiento", example = "2025-05-06T18:30:00")
    private LocalDateTime fecha;

    @Column("distancia_metros")
    @Schema(description = "Distancia recorrida en kilómetros", example = "10.5")
    private Float distanciaMetros;

    @Column("tiempo_segundos")
    @Schema(description = "Duración del entrenamiento en minutos", example = "50.0")
    private Float tiempoSegundos;

    @Column("ritmo_min_km")
    @Schema(description = "Ritmo medio en min/km", example = "4.45")
    private Float ritmoMinKm;

    @Schema(description = "Notas adicionales del entrenamiento", example = "Rodaje suave por la playa")
    private String notas;
}
