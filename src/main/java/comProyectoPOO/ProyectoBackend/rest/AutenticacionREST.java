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

    // Mapea el método POST a la ruta /api/auth/registrar
    @POST
    @Path("/registrar")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registrarEstudiante(RegistroRequest req) {
        org.openxava.jpa.XPersistence.getManager().getTransaction().begin();
        try {
            javax.persistence.EntityManager em = org.openxava.jpa.XPersistence.getManager();
            
            comProyectoPOO.ProyectoBackend.model.registroUsuario.Usuario nuevo = null;
            
            if ("universitario".equals(req.getRol())) {
                comProyectoPOO.ProyectoBackend.model.registroUsuario.EstudianteUniversitario eu = new comProyectoPOO.ProyectoBackend.model.registroUsuario.EstudianteUniversitario();
                eu.setCif(req.getCif());
                eu.setNumeroCedula(req.getNumeroCedula());
                eu.setZona(comProyectoPOO.ProyectoBackend.model.registroUsuario.Zona.valueOf(req.getZona()));
                nuevo = eu;
            } else if ("egresado".equals(req.getRol())) {
                comProyectoPOO.ProyectoBackend.model.registroUsuario.EgresadoSecundaria es = new comProyectoPOO.ProyectoBackend.model.registroUsuario.EgresadoSecundaria();
                es.setNumeroCedula(req.getNumeroCedula());
                es.setTipoInstitucion(req.getTipoInstitucion());
                es.setZona(comProyectoPOO.ProyectoBackend.model.registroUsuario.Zona.valueOf(req.getZona()));
                nuevo = es;
            } else if ("secundaria".equals(req.getRol())) {
                comProyectoPOO.ProyectoBackend.model.registroUsuario.EstudianteSecundaria es = new comProyectoPOO.ProyectoBackend.model.registroUsuario.EstudianteSecundaria();
                es.setTipoInstitucion(req.getTipoInstitucion());
                es.setZona(comProyectoPOO.ProyectoBackend.model.registroUsuario.Zona.valueOf(req.getZona()));
                nuevo = es;
            } else {
                return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\":\"Rol inválido\"}").build();
            }

            nuevo.setNombres(req.getNombres());
            nuevo.setApellidos(req.getApellidos());
            nuevo.setEmail(req.getEmail());
            nuevo.setFechaNacimiento(java.time.LocalDate.parse(req.getFechaNacimiento()));
            nuevo.setDepartamento(req.getDepartamento());
            nuevo.setMunicipio(req.getMunicipio());
            nuevo.setComunidad(req.getComunidad());

            em.persist(nuevo);
            em.flush();
            org.openxava.jpa.XPersistence.getManager().getTransaction().commit();

            return Response.ok("{\"id\":\"" + nuevo.getId() + "\", \"nombres\":\"" + nuevo.getNombres() + "\"}").build();

        } catch (Exception e) {
            org.openxava.jpa.XPersistence.getManager().getTransaction().rollback();
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    // Clase auxiliar interna para capturar el JSON { "cif": "..." } que envía React
    public static class LoginRequest {
        private String cif;
        public String getCif() { return cif; }
        public void setCif(String cif) { this.cif = cif; }
    }

    public static class RegistroRequest {
        private String rol;
        private String nombres;
        private String apellidos;
        private String email;
        private String fechaNacimiento;
        private String departamento;
        private String municipio;
        private String comunidad;
        private String zona;
        private String cif;
        private String numeroCedula;
        private String tipoInstitucion;

        public String getRol() { return rol; } public void setRol(String rol) { this.rol = rol; }
        public String getNombres() { return nombres; } public void setNombres(String nombres) { this.nombres = nombres; }
        public String getApellidos() { return apellidos; } public void setApellidos(String apellidos) { this.apellidos = apellidos; }
        public String getEmail() { return email; } public void setEmail(String email) { this.email = email; }
        public String getFechaNacimiento() { return fechaNacimiento; } public void setFechaNacimiento(String fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }
        public String getDepartamento() { return departamento; } public void setDepartamento(String departamento) { this.departamento = departamento; }
        public String getMunicipio() { return municipio; } public void setMunicipio(String municipio) { this.municipio = municipio; }
        public String getComunidad() { return comunidad; } public void setComunidad(String comunidad) { this.comunidad = comunidad; }
        public String getZona() { return zona; } public void setZona(String zona) { this.zona = zona; }
        public String getCif() { return cif; } public void setCif(String cif) { this.cif = cif; }
        public String getNumeroCedula() { return numeroCedula; } public void setNumeroCedula(String numeroCedula) { this.numeroCedula = numeroCedula; }
        public String getTipoInstitucion() { return tipoInstitucion; } public void setTipoInstitucion(String tipoInstitucion) { this.tipoInstitucion = tipoInstitucion; }
    }
}