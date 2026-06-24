package comProyectoPOO.ProyectoBackend.run;

import org.openxava.util.*;

/**
 * Execute this class to start the application.
 *
 * With OpenXava Studio/Eclipse: Right mouse button > Run As > Java Application
 */

public class ProyectoBackend {

	public static void main(String[] args) throws Exception {
		// DBServer.start("ProyectoBackend-db"); // Comentado: usando PostgreSQL local (bfa_db) en vez de HSQLDB embebido
		AppServer.run("ProyectoBackend");
	}

}
