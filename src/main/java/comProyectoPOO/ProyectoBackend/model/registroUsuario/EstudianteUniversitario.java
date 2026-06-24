package comProyectoPOO.ProyectoBackend.model.registroUsuario;

import javax.persistence.*;

import org.openxava.annotations.*;

import lombok.*;

/**
 * Estudiante universitario activo de la UAM.
 * Hereda de Usuario y agrega su CIF (Carnet de Identificación).
 */
@Entity
@Table(name = "estudiante_universitario")
@DiscriminatorValue("ESTUDIANTE_UNIVERSITARIO")
@Getter @Setter
public class EstudianteUniversitario extends Usuario {

	@Column(length = 50)
	@Required
	private String cif;

}
