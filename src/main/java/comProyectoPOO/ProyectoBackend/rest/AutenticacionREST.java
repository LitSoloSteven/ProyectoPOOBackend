package comProyectoPOO.ProyectoBackend.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import comProyectoPOO.ProyectoBackend.model.registroUsuario.AutenticacionService;
import comProyectoPOO.ProyectoBackend.model.registroUsuario.EstudianteUniversitario;

// Mapea la clase a la ruta /api/auth que espera tu frontend
@Path("/auth")
public class AutenticacionREST {

    // Mapea el método POST a la ruta /api/auth/acceso-universitario
    @POST
    @Path("/acceso-universitario")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response loginEstudiante(LoginRequest request) {

        // Instanciamos tu servicio original
        AutenticacionService authService = new AutenticacionService();

        // Llamamos al método que busca en la base de datos
        EstudianteUniversitario estudiante = authService.accesoUniversitario(request.getCif());

        // Si el estudiante existe, devolvemos un JSON con código 200 (OK)
        if (estudiante != null) {
            return Response.ok(estudiante).build();
        } else {
            // Si no existe, devolvemos un JSON de error con código 401 (No autorizado)
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\":\"Estudiante no encontrado o CIF inválido\"}")
                    .build();
        }
    }

    // Clase auxiliar interna para capturar el JSON { "cif": "..." } que envía React
    public static class LoginRequest {
        private String cif;
        public String getCif() { return cif; }
        public void setCif(String cif) { this.cif = cif; }
    }
}