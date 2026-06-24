package comProyectoPOO.ProyectoBackend.model.resultaTestSeries;

import java.time.*;

import javax.persistence.*;

import org.openxava.annotations.*;

import lombok.*;

/**
 * Registra la respuesta seleccionada por un estudiante/aspirante para
 * una pregunta específica durante una prueba de razonamiento.
 * Equivale a "RespuestaAspirante" del diagrama UML.
 */
@Entity
@Table(name = "respuesta_estudiante")
@Getter @Setter
public class RespuestaEstudiante {

	@Id
	@Column(length = 36)
	@GeneratedValue(generator = "system-uuid")
	@org.hibernate.annotations.GenericGenerator(name = "system-uuid", strategy = "uuid")
	@Hidden
	private String id;

	private LocalDateTime fechaRegistro;

	/**
	 * La prueba/intento a la que pertenece esta respuesta.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "prueba_id")
	private PruebaDeRazonamiento prueba;

	/**
	 * La alternativa elegida por el estudiante.
	 * Se compara con esCorrecta para determinar si es acierto.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "alternativa_id")
	private AlternativaRespuesta alternativaSeleccionada;

}
