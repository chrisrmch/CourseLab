package es.courselab.app.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("lap_extension")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Datos extendidos de una vuelta")
public class LapExtension {

    @Id
    @Column("id_lap_extension")
    @Schema(description = "ID de la extensión de la vuelta", example = "1")
    private Long idLapExtension;

    @Column("id_lap")
    @Schema(description = "ID de la vuelta asociada", example = "42", required = true)
    private Long lapId;

    @Column("avg_speed")
    @Schema(description = "Velocidad media de la vuelta (m/s)", example = "2.5")
    private Float avgSpeed;

    @Column("avg_run_cadence")
    @Schema(description = "Cadencia media de carrera (pasos por minuto)", example = "85")
    private Integer avgRunCadence;

    @Column("max_run_cadence")
    @Schema(description = "Cadencia máxima de carrera (pasos por minuto)", example = "95")
    private Integer maxRunCadence;
}
