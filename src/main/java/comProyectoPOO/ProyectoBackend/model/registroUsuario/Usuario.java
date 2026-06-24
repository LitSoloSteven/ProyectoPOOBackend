package comProyectoPOO.ProyectoBackend.model.registroUsuario;

import javax.persistence.*;

import org.openxava.annotations.*;

import lombok.*;

/**
 * Clase base abstracta para todos los tipos de usuario del sistema.
 * Usa herencia JOINED: cada subclase tendrá su propia tabla
 * con una FK apuntando a la tabla 'usuario'.
 */
@Entity
@Table(name = "usuario")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "tipo_usuario", discriminatorType = DiscriminatorType.STRING)
@Getter @Setter
public abstract class Usuario {

	@Id
	@Column(length = 36)
	@GeneratedValue(generator = "system-uuid")
	@org.hibernate.annotations.GenericGenerator(name = "system-uuid", strategy = "uuid")
	@Hidden
	private String id;

	@Column(length = 100)
	@Required
	private String nombres;

	@Column(length = 100)
	@Required
	private String apellidos;

	@Column(length = 150)
	@Required
	private String email;

}
