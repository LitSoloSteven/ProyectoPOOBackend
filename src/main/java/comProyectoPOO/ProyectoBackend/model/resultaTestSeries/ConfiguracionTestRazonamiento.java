package comProyectoPOO.ProyectoBackend.model.resultaTestSeries;

import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;

import lombok.*;

/**
 * Configuración general del Test de Razonamiento (Forma B).
 * Define el título, instrucciones y el límite de tiempo estricto de 12 minutos.
 * Contiene la colección de preguntas (PreguntaSerie) del test.
 */
@Entity
@Table(name = "configuracion_test_razonamiento")
@Getter @Setter
public class ConfiguracionTestRazonamiento {

	@Id
	@Column(length = 36)
	@GeneratedValue(generator = "system-uuid")
	@org.hibernate.annotations.GenericGenerator(name = "system-uuid", strategy = "uuid")
	@Hidden
	private String id;

	@Column(length = 150)
	@Required
	private String titulo;

	@Column(length = 2000)
	private String instrucciones;

	/**
	 * Límite de tiempo en minutos para completar el test.
	 * Valor por defecto: 12 minutos (regla estricta de la BFA).
	 */
	@Column(nullable = false)
	private Integer tiempoLimiteMinutos = 12;

	@OneToMany(mappedBy = "configuracion", cascade = CascadeType.ALL)
	@OrderBy("orden ASC")
	private List<PreguntaSerie> preguntas = new ArrayList<>();

}
