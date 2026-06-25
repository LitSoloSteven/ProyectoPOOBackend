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
	 * Usuario que realizó esta prueba.
	 * Gracias a la herencia JOINED, OpenXava mostrará los datos específicos
	 * dependiendo de si es EstudianteUniversitario, EgresadoSecundaria, o EstudianteSecundaria.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "usuario_id")
	private Usuario aspirante;

	@OneToMany(mappedBy = "prueba", cascade = CascadeType.ALL)
	private List<RespuestaEstudiante> respuestas = new ArrayList<>();

}
