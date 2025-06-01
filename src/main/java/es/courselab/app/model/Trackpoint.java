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

@Table("trackpoint")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Punto de seguimiento dentro de una vuelta")
public class Trackpoint {

    @Id
    @Column("id_trackpoint")
    @Schema(description = "ID del punto de seguimiento", example = "1")
    private Long idTrackpoint;

    @Column("id_lap")
    @Schema(description = "ID de la vuelta a la que pertenece este punto", example = "42", required = true)
    private Long lapId;

    @Column("time")
    @Schema(description = "Marca de tiempo del punto de seguimiento", example = "2025-05-06T18:50:00")
    private LocalDateTime time;

    @Column("latitude")
    @Schema(description = "Latitud del punto GPS", example = "40.416775")
    private Double latitude;

    @Column("longitude")
    @Schema(description = "Longitud del punto GPS", example = "-3.703790")
    private Double longitude;

    @Column("altitude_meters")
    @Schema(description = "Altitud en metros", example = "250.5")
    private Float altitudeMeters;

    @Column("distance_meters")
    @Schema(description = "Distancia recorrida (desde inicio de la vuelta) en metros", example = "1200.0")
    private Float distanceMeters;

    @Column("heart_rate")
    @Schema(description = "Frecuencia cardíaca en ese punto", example = "155")
    private Integer heartRate;

    @Column("id_extension")
    @Schema(description = "ID de la extensión asociada a este punto", example = "7")
    private Long extensionId;
}
