package comProyectoPOO.ProyectoBackend.model.resultaTestSeries;

import javax.persistence.*;

import org.openxava.annotations.*;

import lombok.*;

/**
 * Tabla de baremos (normas nacionales de Nicaragua) para el Test de Razonamiento.
 * Mapea rangos de puntuación directa (RT) a percentiles.
 * 
 * Fuente: "NORMAS NACIONALES DE LA BATERÍA FACTORIAL DE APTITUDES 1992 - NICARAGUA"
 * Columna: R T (Razonamiento Total)
 */
@Entity
@Table(name = "norma_baremo_razonamiento")
@Getter @Setter
public class NormaBaremoRazonamiento {

	@Id
	@Column(length = 36)
	@GeneratedValue(generator = "system-uuid")
	@org.hibernate.annotations.GenericGenerator(name = "system-uuid", strategy = "uuid")
	@Hidden
	private String id;

	/**
	 * Límite inferior del rango de puntuación directa (inclusive).
	 */
	@Column(nullable = false)
	private Integer puntuacionDirectaMinima;

	/**
	 * Límite superior del rango de puntuación directa (inclusive).
	 */
	@Column(nullable = false)
	private Integer puntuacionDirectaMaxima;

	/**
	 * Percentil asignado para este rango de puntuación.
	 */
	@Column(nullable = false)
	private Integer percentilAsignado;

}
