package es.courselab.app.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("lap")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Vuelta o segmento dentro de un entrenamiento")
public class Lap {

    @Id
    @Column("id_lap")
    @Schema(description = "ID de la vuelta", example = "1")
    private Long idLap;

    @Column("id_entrenamiento")
    @Schema(description = "ID del entrenamiento al que pertenece la vuelta", example = "10")
    private Long entrenamientoId;

    @Column("start_time")
    @Schema(description = "Fecha y hora de inicio de la vuelta", example = "2025-05-06T18:45:00")
    private LocalDateTime startTime;

    @Column("total_time_seconds")
    @Schema(description = "Duración total de la vuelta en segundos", example = "75.0")
    private Float totalTimeSeconds;

    @Column("distance_meters")
    @Schema(description = "Distancia recorrida en la vuelta en metros", example = "400.0")
    private Float distanceMeters;

    @Column("max_speed")
    @Schema(description = "Velocidad máxima alcanzada en la vuelta (m/s)", example = "5.2")
    private Float maxSpeed;

    @Schema(description = "Calorías quemadas en la vuelta", example = "15")
    private Integer calories;

    @Column("avg_heart_rate")
    @Schema(description = "Frecuencia cardíaca media (ppm)", example = "150")
    private Integer avgHeartRate;

    @Column("max_heart_rate")
    @Schema(description = "Frecuencia cardíaca máxima (ppm)", example = "165")
    private Integer maxHeartRate;

    @Schema(description = "Intensidad percibida de la vuelta", example = "moderate")
    private String intensity;

    @Column("trigger_method")
    @Schema(description = "Método que inició la vuelta", example = "auto")
    private String triggerMethod;
}
