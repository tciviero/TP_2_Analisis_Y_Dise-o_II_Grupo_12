package modelo;

import java.util.HashMap;


public class Directorio {
	private HashMap<String,Usuario> usuarios ;

	//	private VentanaDirectorio ventana ;
	
	private static Directorio instance =null;
	
	private Directorio() {
		usuarios = new HashMap<String,Usuario>();
		//ventana = new VentanaDirectorio();
	}
	
	public static Directorio getInstance() {
		if(instance==null) {
			instance = new Directorio();
		}
		return instance;
	}
	
	public boolean contieneUsuario(String nickname) {
		return this.usuarios.containsKey(nickname);
	}
	
	public void agregarUsuario(String nickname, Usuario nuevo) {
		this.usuarios.put(nickname, nuevo);
	}

	
	public Usuario devuelveUsuario(String nickname) {
		return this.usuarios.get(nickname);
	}
	
	/*public void mostrarDirectorio(String usuarioSolicitante,Socket socket_solicitante) {
		String[] usuarios_registrados = usuarios.keySet().toArray(new String[0]);
        this.ventana.mostrarDirectorio(usuarios_registrados, seleccionados -> {
            System.out.println("Usuarios seleccionados:");
            for (String u : seleccionados) {
                System.out.println(u);
            }
            
			try {
				DataOutputStream out = new DataOutputStream(socket_solicitante.getOutputStream());
				String mensaje_agendar = "AGENDAR_USUARIO";
				for (String usuario_seleccionado: seleccionados) {
	    			mensaje_agendar = mensaje_agendar + "`" + usuario_seleccionado;
	    			System.out.println(usuario_seleccionado);
				}
    			System.out.println("usuario a agendar: " + mensaje_agendar);
    			out.writeUTF(mensaje_agendar);
			} catch (IOException e) {
				e.printStackTrace();
			}
        },
        
        () -> { //cuando cierre la ventana
        	try {
				DataOutputStream out = new DataOutputStream(socket_solicitante.getOutputStream());
				String mensaje_ventana_cerrada = "VENTANA_CERRADA";
				System.out.println("manda ventana cerrada");
				out.writeUTF(mensaje_ventana_cerrada);
        	} catch (IOException e) {
				e.printStackTrace();
			}
        });
	}*/

	public String[] getUsuarios() {
		return usuarios.keySet().toArray(new String[0]);
	}
	
	public String getDirectorioFormateado() {
	    StringBuilder sb = new StringBuilder();
	    sb.append("DIRECTORIO`");
	    sb.append(usuarios.size());

	    for (String nombre : usuarios.keySet()) {
	        sb.append("`").append(nombre);
	    }

	    return sb.toString();
	}
}
