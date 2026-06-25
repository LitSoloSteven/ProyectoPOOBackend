package comProyectoPOO.ProyectoBackend.model.resultaTestSeries;

import java.time.*;

import javax.persistence.*;

import org.openxava.annotations.*;

import lombok.*;

/**
 * Reporte generado por un psicólogo evaluador para un intento de prueba.
 * Parte del submódulo ModuloReportes del diagrama UML.
 * 
 * Contiene observaciones del psicólogo y la decisión de si el aspirante
 * requiere una entrevista adicional.
 */
@Entity
@Table(name = "reporte_evaluador")
@Getter @Setter
public class ReporteEvaluador {

	@Id
	@Column(length = 36)
	@GeneratedValue(generator = "system-uuid")
	@org.hibernate.annotations.GenericGenerator(name = "system-uuid", strategy = "uuid")
	@Hidden
	private String id;

	private LocalDateTime fechaGeneracion;

	@Column(length = 2000)
	private String observacionesEvaluador;

	private Boolean requiereEntrevista;

	/**
	 * La prueba de razonamiento evaluada en este reporte.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "prueba_id")
	private PruebaDeRazonamiento prueba;

}
