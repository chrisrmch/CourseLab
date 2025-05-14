package es.courselab.app.model;

import jakarta.persistence.*;
import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "trackpoint")
@Schema(description = "Punto de seguimiento dentro de una vuelta")
public class Trackpoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTrackpoint;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_lap", nullable = false)
    private Lap lap;

    private LocalDateTime time;
    private Double latitude;
    private Double longitude;
    private Float altitudeMeters;
    private Float distanceMeters;
    private Integer heartRate;

    @OneToOne(mappedBy = "trackpoint", cascade = CascadeType.ALL)
    private TrackpointExtension extension;
}
