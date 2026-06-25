package comProyectoPOO.ProyectoBackend.rest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.openxava.jpa.XPersistence;
import javax.persistence.EntityManager;

public class AutenticacionServlet extends HttpServlet {

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
        ObjectMapper mapper = new ObjectMapper();

        if ("/registrar".equals(pathInfo)) {
            try {
                EntityManager em = XPersistence.getManager();
                Map<String, Object> reqBody = mapper.readValue(request.getInputStream(), Map.class);
                
                String rol = (String) reqBody.get("rol");
                comProyectoPOO.ProyectoBackend.model.registroUsuario.Usuario nuevo = null;
                
                if ("universitario".equals(rol)) {
                    comProyectoPOO.ProyectoBackend.model.registroUsuario.EstudianteUniversitario eu = new comProyectoPOO.ProyectoBackend.model.registroUsuario.EstudianteUniversitario();
                    eu.setCif((String) reqBody.get("cif"));
                    eu.setNumeroCedula((String) reqBody.get("numeroCedula"));
                    eu.setZona(comProyectoPOO.ProyectoBackend.model.registroUsuario.Zona.valueOf((String) reqBody.get("zona")));
                    nuevo = eu;
                } else if ("egresado".equals(rol)) {
                    comProyectoPOO.ProyectoBackend.model.registroUsuario.EgresadoSecundaria es = new comProyectoPOO.ProyectoBackend.model.registroUsuario.EgresadoSecundaria();
                    es.setNumeroCedula((String) reqBody.get("numeroCedula"));
                    es.setTipoInstitucion((String) reqBody.get("tipoInstitucion"));
                    es.setZona(comProyectoPOO.ProyectoBackend.model.registroUsuario.Zona.valueOf((String) reqBody.get("zona")));
                    nuevo = es;
                } else if ("secundaria".equals(rol)) {
                    comProyectoPOO.ProyectoBackend.model.registroUsuario.EstudianteSecundaria es = new comProyectoPOO.ProyectoBackend.model.registroUsuario.EstudianteSecundaria();
                    es.setTipoInstitucion((String) reqBody.get("tipoInstitucion"));
                    es.setZona(comProyectoPOO.ProyectoBackend.model.registroUsuario.Zona.valueOf((String) reqBody.get("zona")));
                    nuevo = es;
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"error\":\"Rol inválido\"}");
                    return;
                }

                nuevo.setNombres((String) reqBody.get("nombres"));
                nuevo.setApellidos((String) reqBody.get("apellidos"));
                nuevo.setEmail((String) reqBody.get("email"));
                nuevo.setFechaNacimiento(java.time.LocalDate.parse((String) reqBody.get("fechaNacimiento")));
                nuevo.setDepartamento((String) reqBody.get("departamento"));
                nuevo.setMunicipio((String) reqBody.get("municipio"));
                nuevo.setComunidad((String) reqBody.get("comunidad"));

                em.persist(nuevo);
                em.flush();

                out.print("{\"id\":\"" + nuevo.getId() + "\", \"nombres\":\"" + escapeJson(nuevo.getNombres()) + "\"}");

            } catch (Exception e) {
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"" + escapeJson(e.getMessage() != null ? e.getMessage() : e.toString()) + "\"}");
            }
        } else if ("/acceso-universitario".equals(pathInfo)) {
            // Este endpoint podría utilizarse si quisieran login de alguien ya registrado
            try {
                Map<String, Object> reqBody = mapper.readValue(request.getInputStream(), Map.class);
                String cif = (String) reqBody.get("cif");
                EntityManager em = XPersistence.getManager();
                comProyectoPOO.ProyectoBackend.model.registroUsuario.EstudianteUniversitario estudiante = em.createQuery("SELECT e FROM EstudianteUniversitario e WHERE e.cif = :cif", comProyectoPOO.ProyectoBackend.model.registroUsuario.EstudianteUniversitario.class)
                    .setParameter("cif", cif)
                    .setMaxResults(1)
                    .getSingleResult();
                
                out.print("{\"id\":\"" + estudiante.getId() + "\", \"nombres\":\"" + escapeJson(estudiante.getNombres()) + "\"}");
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.print("{\"error\":\"Estudiante no encontrado o CIF inválido\"}");
            }
        } else if ("/login-administrativo".equals(pathInfo)) {
            try {
                Map<String, Object> reqBody = mapper.readValue(request.getInputStream(), Map.class);
                String cif = (String) reqBody.get("cif");
                String contrasena = (String) reqBody.get("contrasena");
                EntityManager em = XPersistence.getManager();
                comProyectoPOO.ProyectoBackend.model.registroUsuario.Evaluador evaluador = em.createQuery("SELECT e FROM Evaluador e WHERE e.cif = :cif", comProyectoPOO.ProyectoBackend.model.registroUsuario.Evaluador.class)
                    .setParameter("cif", cif)
                    .setMaxResults(1)
                    .getSingleResult();
                
                // Nota: En un sistema real usaríamos BCrypt. Aquí verificamos coincidencia exacta o básica
                if (evaluador.getContrasenaEncriptada() != null && evaluador.getContrasenaEncriptada().equals(contrasena)) {
                    out.print("{\"id\":\"" + evaluador.getId() + "\", \"nombres\":\"" + escapeJson(evaluador.getNombres()) + "\"}");
                } else {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    out.print("{\"error\":\"Contraseña incorrecta\"}");
                }
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.print("{\"error\":\"Evaluador no encontrado o CIF inválido\"}");
            }
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            out.print("{\"error\":\"Ruta no encontrada en el AutenticacionServlet\"}");
        }
    }
}
