package modelo.usuario;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MetodoPersistenciaUsuarios {
	private static final String ARCHIVO = "metodos_persistencia_usuarios.txt";
	private static MetodoPersistenciaUsuarios instance = null;
	private Map<String, String> usuarios_metodos; 
	
	private MetodoPersistenciaUsuarios() {
		this.usuarios_metodos = new HashMap<>();
	}
	
	public static synchronized MetodoPersistenciaUsuarios get_instance() {
		if(instance==null)
			instance = new MetodoPersistenciaUsuarios();
		return instance;
	}
	
	public String buscarMetodoUsuario(String usuario) {
		return usuarios_metodos.get(usuario);
	}

	public void guardarUsuarioEnArchivo(String usuario, String metodo) {
	    Map<String, String> datos = new HashMap<>();

	    // Leer el archivo actual si existe
	    File archivo = new File(ARCHIVO);
	    if (archivo.exists()) {
	        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
	            String linea;
	            while ((linea = reader.readLine()) != null) {
	                String[] partes = linea.split(":");
	                if (partes.length == 2) {
	                    datos.put(partes[0], partes[1]);
	                }
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }

	    // Agregar o actualizar el usuario
	    datos.put(usuario, metodo);

	    // Reescribir el archivo completo
	    try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARCHIVO))) {
	        for (Map.Entry<String, String> entry : datos.entrySet()) {
	            writer.write(entry.getKey() + ":" + entry.getValue());
	            writer.newLine();
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	public void cargarDesdeArchivo() {
	    this.usuarios_metodos.clear(); // limpia el mapa actual
	    File archivo = new File(ARCHIVO);
	    if (!archivo.exists()) return;

	    try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
	        String linea;
	        while ((linea = reader.readLine()) != null) {
	            String[] partes = linea.split(":");
	            if (partes.length == 2) {
	                this.usuarios_metodos.put(partes[0], partes[1]);
	            }
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
}
