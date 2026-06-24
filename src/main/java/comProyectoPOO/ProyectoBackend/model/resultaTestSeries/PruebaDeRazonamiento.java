package comProyectoPOO.ProyectoBackend.model.resultaTestSeries;

import java.time.*;
import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;

import comProyectoPOO.ProyectoBackend.model.registroUsuario.*;

import lombok.*;

/**
 * Representa un intento/sesión de un aspirante al realizar el Test de Razonamiento.
 * Equivale a "IntentoAspirante" del diagrama UML.
 * Almacena tiempos de inicio/fin, estado, puntuación directa obtenida y percentil.
 */
@Entity
@Table(name = "prueba_de_razonamiento")
@Getter @Setter
public class PruebaDeRazonamiento {

	@Id
	@Column(length = 36)
	@GeneratedValue(generator = "system-uuid")
	@org.hibernate.annotations.GenericGenerator(name = "system-uuid", strategy = "uuid")
	@Hidden
	private String id;

	/**
	 * Identificador externo del aspirante (para quienes no son estudiantes UAM).
	 *
	 * <p><b>Restricción de Exclusión Mutua (XOR):</b> Este campo y {@link #estudiante}
	 * son mutuamente excluyentes. Un intento de prueba debe pertenecer a exactamente uno
	 * de los dos: un {@code EstudianteUniversitario} registrado (vía {@link #estudiante})
	 * o un aspirante externo (vía este campo). Nunca ambos simultáneamente ni ambos nulos.</p>
	 *
	 * @see #estudiante
	 */
	@Column(length = 100)
	private String identificadorAspiranteExterno;

	private LocalDateTime horaInicio;

	private LocalDateTime horaFinalizacion;

	/**
	 * Estado del intento: PENDIENTE, EN_PROGRESO, FINALIZADO, CANCELADO.
	 */
	@Column(length = 30)
	private String estado;

	/**
	 * Puntuación directa total (RT) = cantidad de aciertos.
	 */
	private Integer puntuacionDirectaObtenida;

	/**
	 * Percentil obtenido según la tabla de baremos de Nicaragua.
	 */
	private Integer percentilObtenido;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "configuracion_id")
	private ConfiguracionTestRazonamiento configuracion;

	/**
	 * Estudiante universitario UAM que realizó esta prueba.
	 *
	 * <p><b>Restricción de Exclusión Mutua (XOR):</b> Este campo y
	 * {@link #identificadorAspiranteExterno} son mutuamente excluyentes.
	 * Un intento de prueba debe pertenecer a exactamente uno de los dos:
	 * un estudiante UAM (vía este campo) o un aspirante externo
	 * (vía {@link #identificadorAspiranteExterno}). Nunca ambos simultáneamente
	 * ni ambos nulos.</p>
	 *
	 * @see #identificadorAspiranteExterno
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "estudiante_id")
	private EstudianteUniversitario estudiante;

	@OneToMany(mappedBy = "prueba", cascade = CascadeType.ALL)
	private List<RespuestaEstudiante> respuestas = new ArrayList<>();

}
