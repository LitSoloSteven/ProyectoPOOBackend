package comProyectoPOO.ProyectoBackend.model.registroUsuario;

import javax.persistence.*;

import org.openxava.annotations.*;

import lombok.*;

/**
 * Evaluador del sistema (anteriormente Psicólogo).
 * Tiene credenciales de acceso administrativo y número de colegiado.
 */
@Entity
@Table(name = "evaluador")
@DiscriminatorValue("EVALUADOR")
@Getter @Setter
public class Evaluador extends Usuario {

	@Column(length = 50)
	@Required
	private String cif; // Funciona como número de cédula según el requerimiento

	@Column(length = 255)
	@Required
	private String contrasenaEncriptada;

	@Column(length = 50)
	@Required
	private String numeroColegiado;

}
