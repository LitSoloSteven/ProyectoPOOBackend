package comProyectoPOO.ProyectoBackend.model.registroUsuario;

import javax.persistence.*;

import org.openxava.jpa.*;

/**
 * Servicio de autenticación para el sistema BFA.
 * Gestiona login administrativo (psicólogo), acceso de estudiantes
 * y registro de aspirantes externos.
 *
 * Según el diagrama UML:
 * - loginAdministrativo(cif, contrasena) : Evaluador
 * 
 * TODO: En un sistema en producción, las contraseñas deben estar encriptadas (ej. con BCrypt)
 * y la comparación debe usar un PasswordEncoder.
 */
public class AutenticacionService {

	/**
	 * Intenta autenticar a un Psicólogo / Administrador.
	 * 
	 * @param cif El CIF del psicólogo.
	 * @param contrasena La contraseña en texto plano (para el prototipo).
	 * @return Evaluador autenticado, o null si las credenciales son inválidas
	 */
	public Evaluador loginAdministrativo(String cif, String contrasena) {
		EntityManager em = XPersistence.getManager();
		try {
			Evaluador evaluador = (Evaluador) em.createQuery(
				"SELECT p FROM Evaluador p WHERE p.cif = :cif")
				.setParameter("cif", cif)
				.getSingleResult();

			if (evaluador != null && verificarContrasena(contrasena, evaluador.getContrasenaEncriptada())) {
				return evaluador;
			}
			return null;
		} catch (NoResultException e) {
			return null;
		}
	}

	/**
	 * Permite el acceso de un estudiante universitario mediante su CIF.
	 * No requiere contraseña (acceso por identificación institucional).
	 *
	 * @param cif Carnet de identificación del estudiante
	 * @return EstudianteUniversitario encontrado, o null si no existe
	 */
	public EstudianteUniversitario accesoUniversitario(String cif) {
		try {
			EntityManager em = XPersistence.getManager();
			return (EstudianteUniversitario) em.createQuery(
				"SELECT e FROM EstudianteUniversitario e WHERE e.cif = :cif")
				.setParameter("cif", cif)
				.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	/**
	 * Registra un nuevo aspirante externo en el sistema.
	 * Puede ser un EgresadoSecundaria o EstudianteSecundaria.
	 *
	 * @param nombres Nombres del aspirante
	 * @param apellidos Apellidos del aspirante
	 * @param email Correo electrónico
	 * @param nombreEscuela Nombre de la escuela secundaria
	 * @param anioGraduacion Año de graduación (null si aún es estudiante activo)
	 * @param gradoActual Grado actual (null si ya egresó)
	 * @return Usuario registrado
	 */
	public Usuario registrarAspiranteExterno(String nombres, String apellidos,
			String email, String nombreEscuela, Integer anioGraduacion, Integer gradoActual) {
		EntityManager em = XPersistence.getManager();

		Usuario nuevo;
		if (anioGraduacion != null) {
			// Es un egresado de secundaria
			EgresadoSecundaria egresado = new EgresadoSecundaria();
			egresado.setNombreEscuelaSecundaria(nombreEscuela);
			egresado.setAnioGraduacion(anioGraduacion);
			nuevo = egresado;
		} else {
			// Es un estudiante de secundaria activo
			EstudianteSecundaria estudiante = new EstudianteSecundaria();
			estudiante.setNombreEscuelaSecundaria(nombreEscuela);
			estudiante.setAnioCursado(gradoActual);
			nuevo = estudiante;
		}

		nuevo.setNombres(nombres);
		nuevo.setApellidos(apellidos);
		nuevo.setEmail(email);

		em.persist(nuevo);
		return nuevo;
	}

	/**
	 * Verifica si la contraseña proporcionada coincide con la almacenada.
	 * TODO: Implementar hashing real (BCrypt, etc.) en producción.
	 */
	private boolean verificarContrasena(String contrasenaPlana, String contrasenaEncriptada) {
		return contrasenaPlana != null && contrasenaPlana.equals(contrasenaEncriptada);
	}

}
