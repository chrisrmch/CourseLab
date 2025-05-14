package es.courselab.app.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "lap")
@Schema(description = "Vuelta o segmento dentro de un entrenamiento")
public class Lap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idLap;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_entrenamiento", nullable = false)
    private Actividad entrenamiento;

    private LocalDateTime startTime;
    private Float totalTimeSeconds;
    private Float distanceMeters;
    private Float maxSpeed;
    private Integer calories;
    private Integer avgHeartRate;
    private Integer maxHeartRate;
    private String intensity;
    private String triggerMethod;

    @OneToOne(mappedBy = "lap", cascade = CascadeType.ALL)
    private LapExtension extension;

    @OneToMany(mappedBy = "lap", cascade = CascadeType.ALL)
    private List<Trackpoint> trackpoints;
}
