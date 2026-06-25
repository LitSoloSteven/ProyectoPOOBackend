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
            } finally {
                em.close();
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
        response.setContentType("application/json");
        response.setHeader("Access-Control-Allow-Origin", "*");
        PrintWriter out = response.getWriter();
        out.print("{\"status\":\"success\", \"message\":\"POST recibido por el Servlet nativo\"}");
    }
    
    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Accept");
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
