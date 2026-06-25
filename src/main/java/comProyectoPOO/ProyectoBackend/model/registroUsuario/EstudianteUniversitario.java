package comProyectoPOO.ProyectoBackend.model.registroUsuario;

import javax.persistence.*;

import org.openxava.annotations.*;
import javax.validation.constraints.Pattern;

import lombok.*;

/**
 * Estudiante universitario activo de la UAM.
 * Hereda de Usuario y agrega su CIF y número de cédula.
 */
@Entity
@Table(name = "estudiante_universitario")
@DiscriminatorValue("ESTUDIANTE_UNIVERSITARIO")
@Getter @Setter
public class EstudianteUniversitario extends Usuario {

	@Column(length = 50)
	@Required
	private String cif;

	@Column(length = 50)
	@Required
	@Pattern(regexp = "^\\d{3}-\\d{6}-\\d{4}[A-Za-z]$", message = "Formato de cédula incorrecto. Debe ser 001-080108-1047W")
	private String numeroCedula;

	@Enumerated(EnumType.STRING)
	@Required
	private Zona zona;

}
