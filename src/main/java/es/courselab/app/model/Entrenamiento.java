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
@Table(name = "entrenamiento")
@Schema(description = "Representa una sesión de entrenamiento realizada por el usuario")
public class Entrenamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID del entrenamiento", example = "1")
    private Long idEntrenamiento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    @JsonBackReference
    @Schema(description = "Usuario que realizó el entrenamiento")
    private User usuario;

    @Schema(description = "Fecha del entrenamiento", example = "2025-05-06T18:30:00")
    private LocalDateTime fecha;

    @Schema(description = "Distancia recorrida en kilómetros", example = "10.5")
    private Float distanciaKm;

    @Schema(description = "Duración del entrenamiento en minutos", example = "50.0")
    private Float tiempoMin;

    @Schema(description = "Ritmo medio en min/km", example = "4.45")
    private Float ritmoMinKm;

    @Schema(description = "Notas adicionales del entrenamiento", example = "Rodaje suave por la playa")
    private String notas;
}