package comProyectoPOO.ProyectoBackend.rest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.openxava.jpa.XPersistence;

import comProyectoPOO.ProyectoBackend.model.registroUsuario.EstudianteUniversitario;
import comProyectoPOO.ProyectoBackend.model.resultaTestSeries.AlternativaRespuesta;
import comProyectoPOO.ProyectoBackend.model.resultaTestSeries.ConfiguracionTestRazonamiento;
import comProyectoPOO.ProyectoBackend.model.resultaTestSeries.MotorCalificacionService;
import comProyectoPOO.ProyectoBackend.model.resultaTestSeries.PreguntaSerie;
import comProyectoPOO.ProyectoBackend.model.resultaTestSeries.PruebaDeRazonamiento;
import comProyectoPOO.ProyectoBackend.model.resultaTestSeries.RespuestaEstudiante;

@Path("/test-razonamiento")
public class TestRazonamientoREST {

    @GET
    @Path("/configuracion")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getConfiguracion() {
        EntityManager em = XPersistence.getManager();
        try {
            ConfiguracionTestRazonamiento config = em.createQuery(
                "FROM ConfiguracionTestRazonamiento", ConfiguracionTestRazonamiento.class)
                .setMaxResults(1)
                .getSingleResult();

            // Mapeo a DTOs para simplificar JSON de salida
            ConfiguracionDTO dto = new ConfiguracionDTO();
            dto.setId(config.getId());
            dto.setTitulo(config.getTitulo());
            dto.setTiempoLimiteMinutos(config.getTiempoLimiteMinutos());
            
            List<PreguntaDTO> preguntasDTO = new ArrayList<>();
            for (PreguntaSerie p : config.getPreguntas()) {
                PreguntaDTO pdto = new PreguntaDTO();
                pdto.setId(p.getId());
                pdto.setOrden(p.getOrden());
                pdto.setEnunciado(p.getEnunciado());
                
                List<AlternativaDTO> alternativasDTO = new ArrayList<>();
                for (AlternativaRespuesta a : p.getAlternativas()) {
                    AlternativaDTO adto = new AlternativaDTO();
                    adto.setId(a.getId());
                    adto.setLetra(a.getLetra());
                    adto.setTexto(a.getTexto());
                    alternativasDTO.add(adto);
                }
                pdto.setAlternativas(alternativasDTO);
                preguntasDTO.add(pdto);
            }
            dto.setPreguntas(preguntasDTO);

            return Response.ok(dto).build();
        } catch (NoResultException e) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity("{\"error\":\"No hay configuraciones de test creadas\"}")
                .build();
        }
    }

    @POST
    @Path("/iniciar")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response iniciarPrueba(IniciarRequest request) {
        EntityManager em = XPersistence.getManager();
        
        try {
            // Obtener la configuración actual
            ConfiguracionTestRazonamiento config = em.createQuery(
                "FROM ConfiguracionTestRazonamiento", ConfiguracionTestRazonamiento.class)
                .setMaxResults(1)
                .getSingleResult();
                
            // Obtener estudiante
            EstudianteUniversitario estudiante = null;
            if (request.getEstudianteId() != null && !request.getEstudianteId().trim().isEmpty()) {
                estudiante = em.find(EstudianteUniversitario.class, request.getEstudianteId());
            }

            PruebaDeRazonamiento prueba = new PruebaDeRazonamiento();
            prueba.setConfiguracion(config);
            prueba.setEstudiante(estudiante);
            prueba.setHoraInicio(LocalDateTime.now());
            prueba.setEstado("EN_PROGRESO");

            em.persist(prueba);
            em.flush();

            // Devolver ID de la prueba
            return Response.ok("{\"id\":\"" + prueba.getId() + "\", \"horaInicio\":\"" + prueba.getHoraInicio().toString() + "\"}").build();

        } catch (NoResultException e) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity("{\"error\":\"Configuración no encontrada\"}")
                .build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity("{\"error\":\"Error interno al iniciar prueba\"}").build();
        }
    }

    @POST
    @Path("/finalizar")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response finalizarPrueba(FinalizarRequest request) {
        EntityManager em = XPersistence.getManager();
        
        try {
            PruebaDeRazonamiento prueba = em.find(PruebaDeRazonamiento.class, request.getPruebaId());
            if (prueba == null) {
                return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"Prueba no encontrada\"}")
                    .build();
            }

            prueba.setHoraFinalizacion(LocalDateTime.now());

            // Procesar respuestas
            if (request.getRespuestas() != null) {
                for (RespuestaItem item : request.getRespuestas()) {
                    RespuestaEstudiante respuesta = new RespuestaEstudiante();
                    respuesta.setPrueba(prueba);
                    respuesta.setFechaRegistro(LocalDateTime.now());
                    
                    AlternativaRespuesta alternativa = em.find(AlternativaRespuesta.class, item.getAlternativaId());
                    respuesta.setAlternativaSeleccionada(alternativa);
                    
                    em.persist(respuesta);
                }
            }
            em.flush(); // Importante: Guardar las respuestas en BD antes de calificar

            // Calificar usando el servicio existente
            MotorCalificacionService motor = new MotorCalificacionService();
            Integer puntuacion = motor.calificarCompleto(prueba);

            // Devolver resultado
            String responseJson = String.format(
                "{\"estado\":\"%s\", \"puntuacionDirecta\":%d, \"percentil\":%d}",
                prueba.getEstado(),
                puntuacion,
                prueba.getPercentilObtenido() == null ? 0 : prueba.getPercentilObtenido()
            );

            return Response.ok(responseJson).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity("{\"error\":\"Error interno al finalizar prueba\"}").build();
        }
    }

    // --- DTOs para Request y Response

    public static class ConfiguracionDTO {
        private String id;
        private String titulo;
        private Integer tiempoLimiteMinutos;
        private List<PreguntaDTO> preguntas;
        public String getId() { return id; } public void setId(String id) { this.id = id; }
        public String getTitulo() { return titulo; } public void setTitulo(String titulo) { this.titulo = titulo; }
        public Integer getTiempoLimiteMinutos() { return tiempoLimiteMinutos; } public void setTiempoLimiteMinutos(Integer t) { this.tiempoLimiteMinutos = t; }
        public List<PreguntaDTO> getPreguntas() { return preguntas; } public void setPreguntas(List<PreguntaDTO> p) { this.preguntas = p; }
    }

    public static class PreguntaDTO {
        private String id;
        private Integer orden;
        private String enunciado;
        private List<AlternativaDTO> alternativas;
        public String getId() { return id; } public void setId(String id) { this.id = id; }
        public Integer getOrden() { return orden; } public void setOrden(Integer orden) { this.orden = orden; }
        public String getEnunciado() { return enunciado; } public void setEnunciado(String enunciado) { this.enunciado = enunciado; }
        public List<AlternativaDTO> getAlternativas() { return alternativas; } public void setAlternativas(List<AlternativaDTO> a) { this.alternativas = a; }
    }

    public static class AlternativaDTO {
        private String id;
        private String letra;
        private String texto;
        public String getId() { return id; } public void setId(String id) { this.id = id; }
        public String getLetra() { return letra; } public void setLetra(String letra) { this.letra = letra; }
        public String getTexto() { return texto; } public void setTexto(String texto) { this.texto = texto; }
    }

    public static class IniciarRequest {
        private String estudianteId;
        public String getEstudianteId() { return estudianteId; } public void setEstudianteId(String e) { this.estudianteId = e; }
    }

    public static class FinalizarRequest {
        private String pruebaId;
        private List<RespuestaItem> respuestas;
        public String getPruebaId() { return pruebaId; } public void setPruebaId(String p) { this.pruebaId = p; }
        public List<RespuestaItem> getRespuestas() { return respuestas; } public void setRespuestas(List<RespuestaItem> r) { this.respuestas = r; }
    }

    public static class RespuestaItem {
        private String preguntaId;
        private String alternativaId;
        public String getPreguntaId() { return preguntaId; } public void setPreguntaId(String p) { this.preguntaId = p; }
        public String getAlternativaId() { return alternativaId; } public void setAlternativaId(String a) { this.alternativaId = a; }
    }
}
