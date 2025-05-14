package es.courselab.app.model;

import jakarta.persistence.*;
import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "lap_extension")
@Schema(description = "Datos extendidos de una vuelta")
public class LapExtension {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idLapExtension;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_lap", nullable = false)
    private Lap lap;

    private Float avgSpeed;
    private Integer avgRunCadence;
    private Integer maxRunCadence;
}
