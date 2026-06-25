package comProyectoPOO.ProyectoBackend.rest;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openxava.jpa.XPersistence;

import comProyectoPOO.ProyectoBackend.model.resultaTestSeries.AlternativaRespuesta;
import comProyectoPOO.ProyectoBackend.model.resultaTestSeries.ConfiguracionTestRazonamiento;
import comProyectoPOO.ProyectoBackend.model.resultaTestSeries.PreguntaSerie;

public class TestRazonamientoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        // CORS Headers just in case (though Vite proxy is used)
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        
        PrintWriter out = response.getWriter();

        if ("/configuracion".equals(pathInfo)) {
            EntityManager em = XPersistence.getManager();
            try {
                ConfiguracionTestRazonamiento config = em.createQuery(
                    "FROM ConfiguracionTestRazonamiento", ConfiguracionTestRazonamiento.class)
                    .setMaxResults(1)
                    .getSingleResult();

                // Usamos el ObjectMapper de Jackson (incluido en OpenXava) para un JSON perfecto
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                
                // Mapear manualmente a un Map para evitar problemas de proxy lazy o recursividad
                java.util.Map<String, Object> responseMap = new java.util.HashMap<>();
                responseMap.put("id", config.getId());
                responseMap.put("titulo", config.getTitulo());
                responseMap.put("tiempoLimiteMinutos", config.getTiempoLimiteMinutos());
                
                List<java.util.Map<String, Object>> preguntasList = new java.util.ArrayList<>();
                for (PreguntaSerie p : config.getPreguntas()) {
                    java.util.Map<String, Object> pMap = new java.util.HashMap<>();
                    pMap.put("id", p.getId());
                    pMap.put("orden", p.getOrden());
                    pMap.put("enunciado", p.getEnunciado());
                    
                    List<java.util.Map<String, Object>> altList = new java.util.ArrayList<>();
                    for (AlternativaRespuesta a : p.getAlternativas()) {
                        java.util.Map<String, Object> aMap = new java.util.HashMap<>();
                        aMap.put("id", a.getId());
                        aMap.put("letra", a.getLetra());
                        aMap.put("texto", a.getTexto());
                        altList.add(aMap);
                    }
                    pMap.put("alternativas", altList);
                    preguntasList.add(pMap);
                }
                responseMap.put("preguntas", preguntasList);
                
                out.print(mapper.writeValueAsString(responseMap));
            } catch (javax.persistence.NoResultException e) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"error\":\"No hay configuraciones de test creadas\"}");
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print("{\"error\":\"Error interno: " + e.getMessage() + "\"}");
                e.printStackTrace();
            }
        } else if ("/resultados-globales".equals(pathInfo)) {
            EntityManager em = XPersistence.getManager();
            try {
                java.util.List<comProyectoPOO.ProyectoBackend.model.resultaTestSeries.PruebaDeRazonamiento> pruebas = em.createQuery(
                    "SELECT p FROM PruebaDeRazonamiento p WHERE p.estado = 'FINALIZADO' ORDER BY p.horaInicio DESC", 
                    comProyectoPOO.ProyectoBackend.model.resultaTestSeries.PruebaDeRazonamiento.class)
                    .getResultList();

                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                
                List<java.util.Map<String, Object>> resultList = new java.util.ArrayList<>();
                for (comProyectoPOO.ProyectoBackend.model.resultaTestSeries.PruebaDeRazonamiento p : pruebas) {
                    java.util.Map<String, Object> map = new java.util.HashMap<>();
                    map.put("id", p.getId());
                    
                    comProyectoPOO.ProyectoBackend.model.registroUsuario.Usuario aspi = p.getAspirante();
                    if (aspi != null) {
                        map.put("aspiranteNombres", aspi.getNombres());
                        map.put("aspiranteApellidos", aspi.getApellidos());
                        
                        Object unproxiedAspi = org.hibernate.Hibernate.unproxy(aspi);
                        
                        if (unproxiedAspi instanceof comProyectoPOO.ProyectoBackend.model.registroUsuario.EstudianteUniversitario) {
                            comProyectoPOO.ProyectoBackend.model.registroUsuario.EstudianteUniversitario eu = (comProyectoPOO.ProyectoBackend.model.registroUsuario.EstudianteUniversitario) unproxiedAspi;
                            map.put("tipoAspirante", "Universitario");
                            map.put("cedulaOCif", eu.getCif());
                            map.put("zona", eu.getZona() != null ? eu.getZona().name() : "N/A");
                            map.put("institucion", "UAM");
                        } else if (unproxiedAspi instanceof comProyectoPOO.ProyectoBackend.model.registroUsuario.EgresadoSecundaria) {
                            comProyectoPOO.ProyectoBackend.model.registroUsuario.EgresadoSecundaria es = (comProyectoPOO.ProyectoBackend.model.registroUsuario.EgresadoSecundaria) unproxiedAspi;
                            map.put("tipoAspirante", "Secundaria Egresado");
                            map.put("cedulaOCif", es.getNumeroCedula());
                            map.put("zona", es.getZona() != null ? es.getZona().name() : "N/A");
                            map.put("institucion", es.getTipoInstitucion() != null ? es.getTipoInstitucion() : "N/A");
                        } else if (unproxiedAspi instanceof comProyectoPOO.ProyectoBackend.model.registroUsuario.EstudianteSecundaria) {
                            comProyectoPOO.ProyectoBackend.model.registroUsuario.EstudianteSecundaria es2 = (comProyectoPOO.ProyectoBackend.model.registroUsuario.EstudianteSecundaria) unproxiedAspi;
                            map.put("tipoAspirante", "Estudiante Secundaria");
                            map.put("cedulaOCif", "N/A"); 
                            map.put("zona", es2.getZona() != null ? es2.getZona().name() : "N/A");
                            map.put("institucion", es2.getTipoInstitucion() != null ? es2.getTipoInstitucion() : "N/A");
                        } else {
                            map.put("tipoAspirante", "Desconocido");
                            map.put("cedulaOCif", "N/A");
                            map.put("zona", "N/A");
                            map.put("institucion", "N/A");
                        }
                    } else {
                        map.put("aspiranteNombres", "Desconocido");
                        map.put("aspiranteApellidos", "");
                        map.put("tipoAspirante", "N/A");
                        map.put("cedulaOCif", "N/A");
                        map.put("zona", "N/A");
                        map.put("institucion", "N/A");
                    }
                    
                    if (p.getHoraInicio() != null) {
                        map.put("horaInicio", new int[] {
                            p.getHoraInicio().getYear(),
                            p.getHoraInicio().getMonthValue(),
                            p.getHoraInicio().getDayOfMonth(),
                            p.getHoraInicio().getHour(),
                            p.getHoraInicio().getMinute()
                        });
                    }
                    
                    map.put("puntuacionDirecta", p.getPuntuacionDirectaObtenida());
                    map.put("percentil", p.getPercentilObtenido());
                    
                    List<java.util.Map<String, Object>> respuestasList = new java.util.ArrayList<>();
                    if (p.getRespuestas() != null) {
                        for (comProyectoPOO.ProyectoBackend.model.resultaTestSeries.RespuestaEstudiante r : p.getRespuestas()) {
                            java.util.Map<String, Object> rMap = new java.util.HashMap<>();
                            if (r.getAlternativaSeleccionada() != null) {
                                comProyectoPOO.ProyectoBackend.model.resultaTestSeries.AlternativaRespuesta alt = r.getAlternativaSeleccionada();
                                comProyectoPOO.ProyectoBackend.model.resultaTestSeries.PreguntaSerie preg = alt.getPregunta();
                                rMap.put("ordenPregunta", preg.getOrden());
                                rMap.put("letraMarcada", alt.getLetra());
                                rMap.put("esCorrecta", alt.isEsCorrecta());
                                rMap.put("textoRespuesta", alt.getTexto());
                            }
                            respuestasList.add(rMap);
                        }
                    }
                    map.put("respuestas", respuestasList);
                    
                    // Buscar si existe un ReporteEvaluador para esta prueba
                    java.util.List<comProyectoPOO.ProyectoBackend.model.resultaTestSeries.ReporteEvaluador> reportes = em.createQuery(
                        "SELECT r FROM ReporteEvaluador r WHERE r.prueba.id = :pid", comProyectoPOO.ProyectoBackend.model.resultaTestSeries.ReporteEvaluador.class)
                        .setParameter("pid", p.getId())
                        .setMaxResults(1)
                        .getResultList();
                        
                    if (!reportes.isEmpty()) {
                        comProyectoPOO.ProyectoBackend.model.resultaTestSeries.ReporteEvaluador rep = reportes.get(0);
                        map.put("tieneReporte", true);
                        map.put("observacionesEvaluador", rep.getObservacionesEvaluador());
                        map.put("requiereEntrevista", rep.getRequiereEntrevista());
                    } else {
                        map.put("tieneReporte", false);
                        map.put("observacionesEvaluador", "");
                        map.put("requiereEntrevista", false);
                    }
                    
                    resultList.add(map);
                }
                out.print(mapper.writeValueAsString(resultList));
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print("{\"error\":\"Error interno: " + escapeJson(e.getMessage()) + "\"}");
                e.printStackTrace();
            }
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            out.print("{\"error\":\"Ruta no encontrada en el Servlet nativo\"}");
        }
    }
    
    private String escapeJson(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        
        PrintWriter out = response.getWriter();
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();

        if ("/iniciar".equals(pathInfo)) {
            EntityManager em = XPersistence.getManager();
            try {
                java.util.Map<String, Object> reqBody = mapper.readValue(request.getInputStream(), java.util.Map.class);
                String estudianteId = (String) reqBody.get("estudianteId");
                
                ConfiguracionTestRazonamiento config = em.createQuery(
                    "FROM ConfiguracionTestRazonamiento", ConfiguracionTestRazonamiento.class)
                    .setMaxResults(1)
                    .getSingleResult();
                    
                comProyectoPOO.ProyectoBackend.model.registroUsuario.Usuario usr = null;
                if (estudianteId != null && !estudianteId.trim().isEmpty()) {
                    usr = em.find(comProyectoPOO.ProyectoBackend.model.registroUsuario.Usuario.class, estudianteId);
                }

                comProyectoPOO.ProyectoBackend.model.resultaTestSeries.PruebaDeRazonamiento prueba = new comProyectoPOO.ProyectoBackend.model.resultaTestSeries.PruebaDeRazonamiento();
                prueba.setConfiguracion(config);
                prueba.setAspirante(usr);
                prueba.setHoraInicio(java.time.LocalDateTime.now());
                prueba.setEstado("EN_PROGRESO");

                em.persist(prueba);
                em.flush();
                
                java.util.Map<String, Object> resMap = new java.util.HashMap<>();
                resMap.put("id", prueba.getId());
                resMap.put("horaInicio", prueba.getHoraInicio().toString());
                out.print(mapper.writeValueAsString(resMap));
            } catch (javax.persistence.NoResultException e) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"error\":\"Configuración no encontrada\"}");
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print("{\"error\":\"" + e.getMessage() + "\"}");
                e.printStackTrace();
            }
        } else if ("/finalizar".equals(pathInfo)) {
            EntityManager em = XPersistence.getManager();
            try {
                java.util.Map<String, Object> reqBody = mapper.readValue(request.getInputStream(), java.util.Map.class);
                String pruebaId = (String) reqBody.get("pruebaId");
                List<java.util.Map<String, String>> respuestas = (List<java.util.Map<String, String>>) reqBody.get("respuestas");
                
                comProyectoPOO.ProyectoBackend.model.resultaTestSeries.PruebaDeRazonamiento prueba = em.find(comProyectoPOO.ProyectoBackend.model.resultaTestSeries.PruebaDeRazonamiento.class, pruebaId);
                if (prueba == null) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print("{\"error\":\"Prueba no encontrada\"}");
                    return;
                }

                prueba.setHoraFinalizacion(java.time.LocalDateTime.now());

                List<comProyectoPOO.ProyectoBackend.model.resultaTestSeries.RespuestaEstudiante> guardadas = new java.util.ArrayList<>();
                if (respuestas != null) {
                    for (java.util.Map<String, String> item : respuestas) {
                        comProyectoPOO.ProyectoBackend.model.resultaTestSeries.RespuestaEstudiante respuesta = new comProyectoPOO.ProyectoBackend.model.resultaTestSeries.RespuestaEstudiante();
                        respuesta.setPrueba(prueba);
                        respuesta.setFechaRegistro(java.time.LocalDateTime.now());
                        
                        AlternativaRespuesta alternativa = em.find(AlternativaRespuesta.class, item.get("alternativaId"));
                        respuesta.setAlternativaSeleccionada(alternativa);
                        
                        em.persist(respuesta);
                        guardadas.add(respuesta);
                    }
                }
                em.flush(); // Guardamos respuestas para que el motor las encuentre

                comProyectoPOO.ProyectoBackend.model.resultaTestSeries.MotorCalificacionService motor = new comProyectoPOO.ProyectoBackend.model.resultaTestSeries.MotorCalificacionService();
                Integer puntuacion = motor.calificarCompleto(prueba, guardadas);
                
                java.util.Map<String, Object> resMap = new java.util.HashMap<>();
                resMap.put("estado", prueba.getEstado());
                resMap.put("puntuacionDirecta", puntuacion);
                resMap.put("percentil", prueba.getPercentilObtenido() == null ? 0 : prueba.getPercentilObtenido());
                out.print(mapper.writeValueAsString(resMap));
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print("{\"error\":\"" + escapeJson(e.getMessage()) + "\"}");
                e.printStackTrace();
            }
        } else if ("/guardar-reporte".equals(pathInfo)) {
            EntityManager em = XPersistence.getManager();
            try {
                java.util.Map<String, Object> reqBody = mapper.readValue(request.getInputStream(), java.util.Map.class);
                String pruebaId = (String) reqBody.get("pruebaId");
                String observaciones = (String) reqBody.get("observaciones");
                Boolean requiereEntrevista = (Boolean) reqBody.get("requiereEntrevista");
                
                comProyectoPOO.ProyectoBackend.model.resultaTestSeries.PruebaDeRazonamiento prueba = em.find(comProyectoPOO.ProyectoBackend.model.resultaTestSeries.PruebaDeRazonamiento.class, pruebaId);
                if (prueba == null) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print("{\"error\":\"Prueba no encontrada\"}");
                    return;
                }
                
                // Buscar reporte existente
                java.util.List<comProyectoPOO.ProyectoBackend.model.resultaTestSeries.ReporteEvaluador> reportes = em.createQuery(
                    "SELECT r FROM ReporteEvaluador r WHERE r.prueba.id = :pid", comProyectoPOO.ProyectoBackend.model.resultaTestSeries.ReporteEvaluador.class)
                    .setParameter("pid", pruebaId)
                    .setMaxResults(1)
                    .getResultList();
                    
                comProyectoPOO.ProyectoBackend.model.resultaTestSeries.ReporteEvaluador reporte;
                if (!reportes.isEmpty()) {
                    reporte = reportes.get(0);
                } else {
                    reporte = new comProyectoPOO.ProyectoBackend.model.resultaTestSeries.ReporteEvaluador();
                    reporte.setPrueba(prueba);
                }
                
                reporte.setFechaGeneracion(java.time.LocalDateTime.now());
                reporte.setObservacionesEvaluador(observaciones);
                reporte.setRequiereEntrevista(requiereEntrevista != null ? requiereEntrevista : false);
                
                if (reportes.isEmpty()) {
                    em.persist(reporte);
                }
                em.flush();
                
                out.print("{\"success\":true}");
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print("{\"error\":\"Error interno: " + escapeJson(e.getMessage()) + "\"}");
                e.printStackTrace();
            }
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            out.print("{\"error\":\"Ruta POST no encontrada en el Servlet nativo\"}");
        }
    }
    
    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Accept");
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
