package comProyectoPOO.ProyectoBackend.model.resultaTestSeries;

import java.time.*;
import java.time.temporal.*;
import java.util.*;

import javax.persistence.*;

import org.openxava.jpa.*;

/**
 * Servicio de calificación del Test de Razonamiento (Forma B).
 * 
 * Regla del Manual de Correcciones BFA:
 * "Pruebas de Razonamiento: Se cuentan únicamente el número de aciertos
 *  por columna: Columna 3 = R1, Columna 4 = R2. R1 + R2 = RT"
 * 
 * En el formato digital, el total de aciertos se calcula directamente
 * como RT (puntuación directa total).
 */
public class MotorCalificacionService {

	/**
	 * Calcula la Puntuación Directa Total (RT) de una prueba de razonamiento.
	 * 
	 * Recorre todas las RespuestaEstudiante del intento y suma 1 punto
	 * por cada respuesta cuya AlternativaRespuesta tenga esCorrecta == true.
	 * 
	 * @param intento La prueba de razonamiento a calificar
	 * @return La puntuación directa total (RT)
	 */
	public Integer calcularPuntuacionDirecta(PruebaDeRazonamiento intento, List<RespuestaEstudiante> respuestas) {
		// Contar aciertos: sumar 1 por cada alternativa seleccionada que sea correcta
		int aciertos = 0;
		if (respuestas != null) {
			for (RespuestaEstudiante respuesta : respuestas) {
				if (respuesta.getAlternativaSeleccionada() != null
						&& respuesta.getAlternativaSeleccionada().isEsCorrecta()) {
					aciertos++;
				}
			}
		}

		// Asignar la puntuación directa al intento
		intento.setPuntuacionDirectaObtenida(aciertos);

		return aciertos;
	}

	/**
	 * Valida si el aspirante completó la prueba dentro del límite de tiempo.
	 * El límite estricto es de 12 minutos según la BFA.
	 *
	 * El límite se convierte a segundos exactos (ej. 12 min = 720 seg).
	 *
	 * @param intento La prueba de razonamiento a validar
	 * @return true si el tiempo fue <= al límite configurado en segundos, false en caso contrario
	 */
	public boolean validarCumplimientoTiempo(PruebaDeRazonamiento intento) {
		if (intento.getHoraInicio() == null || intento.getHoraFinalizacion() == null) {
			return false;
		}

		// Cálculo estricto en segundos para evitar truncamiento de decimales
		long segundosUsados = ChronoUnit.SECONDS.between(
			intento.getHoraInicio(),
			intento.getHoraFinalizacion()
		);

		int limiteMinutos = intento.getConfiguracion() != null
			? intento.getConfiguracion().getTiempoLimiteMinutos()
			: 12; // valor por defecto según BFA

		long limiteEnSegundos = limiteMinutos * 60L;

		return segundosUsados <= limiteEnSegundos;
	}

	/**
	 * Busca en la tabla de baremos el percentil correspondiente a una puntuación directa.
	 * 
	 * @param puntuacionDirecta La puntuación RT obtenida
	 * @return El percentil asignado, o null si no se encuentra en los baremos
	 */
	public Integer calcularPercentil(Integer puntuacionDirecta) {
		EntityManager em = XPersistence.getManager();

		try {
			NormaBaremoRazonamiento norma = (NormaBaremoRazonamiento) em.createQuery(
				"SELECT n FROM NormaBaremoRazonamiento n " +
				"WHERE :pd BETWEEN n.puntuacionDirectaMinima AND n.puntuacionDirectaMaxima " +
				"ORDER BY n.percentilAsignado DESC")
				.setParameter("pd", puntuacionDirecta)
				.setMaxResults(1)
				.getSingleResult();

			return norma.getPercentilAsignado();
		} catch (NoResultException e) {
			return null;
		}
	}

	public Integer calificarCompleto(PruebaDeRazonamiento intento, List<RespuestaEstudiante> respuestas) {
		EntityManager em = XPersistence.getManager();

		// 1. Validar cumplimiento de tiempo
		boolean tiempoValido = validarCumplimientoTiempo(intento);
		if (!tiempoValido) {
			intento.setEstado("TIEMPO_EXCEDIDO");
		} else {
			intento.setEstado("FINALIZADO");
		}

		// 2. Calcular puntuación directa (RT = total de aciertos) usando la lista en memoria
		Integer rt = calcularPuntuacionDirecta(intento, respuestas);

		// 3. Convertir a percentil según baremos de Nicaragua
		Integer percentil = calcularPercentil(rt);
		intento.setPercentilObtenido(percentil);

		// 4. Persistir resultado final
		em.merge(intento);

		return rt;
	}

	/**
	 * Variante para cuando no se proveen las respuestas en memoria (las consulta de BD).
	 */
	public Integer calificarCompleto(PruebaDeRazonamiento intento) {
		EntityManager em = XPersistence.getManager();
		List<RespuestaEstudiante> respuestas = em.createQuery(
			"SELECT r FROM RespuestaEstudiante r " +
			"JOIN FETCH r.alternativaSeleccionada " +
			"WHERE r.prueba.id = :pruebaId",
			RespuestaEstudiante.class)
			.setParameter("pruebaId", intento.getId())
			.getResultList();
		
		return calificarCompleto(intento, respuestas);
	}

}
