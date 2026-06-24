package comProyectoPOO.ProyectoBackend.model.registroUsuario;

import javax.persistence.*;

import org.openxava.annotations.*;

import lombok.*;

/**
 * Psicólogo evaluador del sistema.
 * Tiene credenciales de acceso administrativo y número de colegiado.
 */
@Entity
@Table(name = "psicologo")
@DiscriminatorValue("PSICOLOGO")
@Getter @Setter
public class Psicologo extends Usuario {

	@Column(length = 50)
	@Required
	private String cif;

	@Column(length = 255)
	@Required
	private String contrasenaEncriptada;

	@Column(length = 50)
	@Required
	private String numeroColegiado;

}
