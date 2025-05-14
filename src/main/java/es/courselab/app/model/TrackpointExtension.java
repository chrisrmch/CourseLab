package es.courselab.app.model;

import jakarta.persistence.*;
import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "trackpoint_extension")
@Schema(description = "Datos extendidos de un trackpoint")
public class TrackpointExtension {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTrackpointExtension;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_trackpoint", nullable = false)
    private Trackpoint trackpoint;

    private Float speed;
    private Integer runCadence;
}
