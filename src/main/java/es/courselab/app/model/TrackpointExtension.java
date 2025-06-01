package es.courselab.app.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("trackpoint_extension")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Datos extendidos de un trackpoint")
public class TrackpointExtension {

    @Id
    @Column("id_trackpoint_extension")
    @Schema(description = "ID de la extensión de trackpoint", example = "1")
    private Long idTrackpointExtension;

    @Column("id_trackpoint")
    @Schema(description = "ID del trackpoint al que pertenece esta extensión", example = "42", required = true)
    private Long trackpointId;

    @Column("speed")
    @Schema(description = "Velocidad en el punto de seguimiento (m/s)", example = "3.2")
    private Float speed;

    @Column("run_cadence")
    @Schema(description = "Cadencia de carrera en el punto (pasos por minuto)", example = "85")
    private Integer runCadence;
}
