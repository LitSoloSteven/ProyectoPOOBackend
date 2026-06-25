package comProyectoPOO.ProyectoBackend.model.registroUsuario;

import javax.persistence.*;

import org.openxava.annotations.*;

import lombok.*;

/**
 * Egresado de educación secundaria que aplica como aspirante externo.
 * Hereda de Usuario. Almacena datos de su centro educativo, año de graduación, cédula y tipo de institución.
 */
@Entity
@Table(name = "egresado_secundaria")
@DiscriminatorValue("EGRESADO_SECUNDARIA")
@Getter @Setter
public class EgresadoSecundaria extends Usuario {

	@Column(length = 150)
	private String nombreEscuelaSecundaria;

	private Integer anioGraduacion;

	@Column(length = 50)
	@Required
	private String numeroCedula;

	@Column(length = 50)
	@Required
	private String tipoInstitucion; // Público o Privado

}
