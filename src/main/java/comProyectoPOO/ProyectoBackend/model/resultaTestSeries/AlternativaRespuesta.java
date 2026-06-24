package comProyectoPOO.ProyectoBackend.model.resultaTestSeries;

import javax.persistence.*;

import org.openxava.annotations.*;

import lombok.*;

/**
 * Una alternativa de respuesta (A, B, C o D) para una pregunta del test.
 * Exactamente una alternativa por pregunta tiene esCorrecta = true.
 */
@Entity
@Table(name = "alternativa_respuesta")
@Getter @Setter
public class AlternativaRespuesta {

	@Id
	@Column(length = 36)
	@GeneratedValue(generator = "system-uuid")
	@org.hibernate.annotations.GenericGenerator(name = "system-uuid", strategy = "uuid")
	@Hidden
	private String id;

	@Column(nullable = false)
	private char letra; // 'A', 'B', 'C' o 'D'

	@Column(length = 500, nullable = false)
	@Required
	private String texto;

	/**
	 * Indica si esta alternativa es la respuesta correcta.
	 * Solo una alternativa por pregunta debe tener esCorrecta = true.
	 */
	@Column(nullable = false)
	private boolean esCorrecta = false;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pregunta_id")
	private PreguntaSerie pregunta;

}
