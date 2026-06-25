package comProyectoPOO.ProyectoBackend.model.registroUsuario;

import javax.persistence.*;

import org.openxava.annotations.*;

import lombok.*;

/**
 * Estudiante de educación secundaria.
 * Hereda de Usuario. Almacena datos de su centro educativo, año cursado y tipo de institución.
 */
@Entity
@Table(name = "estudiante_secundaria")
@DiscriminatorValue("ESTUDIANTE_SECUNDARIA")
@Getter @Setter
public class EstudianteSecundaria extends Usuario {

	@Column(length = 150)
	private String nombreEscuelaSecundaria;

	private Integer anioCursado;

	@Column(length = 50)
	@Required
	private String tipoInstitucion; // Público o Privado

	@Enumerated(EnumType.STRING)
	@Required
	private Zona zona;

}
