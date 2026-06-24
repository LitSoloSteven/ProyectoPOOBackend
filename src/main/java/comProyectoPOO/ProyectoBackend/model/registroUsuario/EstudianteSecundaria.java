package comProyectoPOO.ProyectoBackend.model.registroUsuario;

import javax.persistence.*;

import org.openxava.annotations.*;

import lombok.*;

/**
 * Estudiante activo de educación secundaria que aplica como aspirante.
 * Hereda de Usuario. Almacena datos de su centro educativo y grado actual.
 */
@Entity
@Table(name = "estudiante_secundaria")
@DiscriminatorValue("ESTUDIANTE_SECUNDARIA")
@Getter @Setter
public class EstudianteSecundaria extends Usuario {

	@Column(length = 150)
	private String nombreEscuelaSecundaria;

	private Integer gradoActual;

}
