package comProyectoPOO.ProyectoBackend.model.registroUsuario;

import javax.persistence.*;

import org.openxava.annotations.*;
import javax.validation.constraints.Pattern;

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


	@Column(length = 50)
	@Required
	@Pattern(regexp = "^\\d{3}-\\d{6}-\\d{4}[A-Za-z]$", message = "Formato de cédula incorrecto. Debe ser 001-080108-1047W")
	private String numeroCedula;

	@Column(length = 50)
	@Required
	private String tipoInstitucion; // Público o Privado

	@Enumerated(EnumType.STRING)
	@Required
	private Zona zona;

}
