package comProyectoPOO.ProyectoBackend.model.resultaTestSeries;

import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;

import lombok.*;

/**
 * Representa una pregunta (ejercicio) del Test de Razonamiento Forma B.
 * Cada pregunta tiene un número de orden (26 al 55), un enunciado
 * y cuatro alternativas de respuesta (A, B, C, D).
 */
@Entity
@Table(name = "pregunta_serie")
@Getter @Setter
public class PreguntaSerie {

	@Id
	@Column(length = 36)
	@GeneratedValue(generator = "system-uuid")
	@org.hibernate.annotations.GenericGenerator(name = "system-uuid", strategy = "uuid")
	@Hidden
	private String id;

	@Column(nullable = false)
	private Integer orden;

	@Column(length = 1000, nullable = false)
	@Required
	private String enunciado;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "configuracion_id")
	private ConfiguracionTestRazonamiento configuracion;

	@OneToMany(mappedBy = "pregunta", cascade = CascadeType.ALL)
	@OrderBy("letra ASC")
	private List<AlternativaRespuesta> alternativas = new ArrayList<>();

}
