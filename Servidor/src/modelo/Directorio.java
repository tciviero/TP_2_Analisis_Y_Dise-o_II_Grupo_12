package modelo;

import java.util.ArrayList;

import controlador.ControladorServidor;


public class Directorio {
	private ArrayList<Usuario> usuarios ;
	//Cambie el directorio de hashmap a array porque necesito
	//Que esten ordenados alfabeticamente

	//	private VentanaDirectorio ventana ;
	
	private static Directorio instance =null;
	
	private Directorio() {
		usuarios = new ArrayList<Usuario>();
		//ventana = new VentanaDirectorio();
	}
	
	public static Directorio getInstance() {
		if(instance==null) {
			instance = new Directorio();
		}
		return instance;
	}
	
	public boolean contieneUsuario(String nickname) {
		for (Usuario u : usuarios) {
	        if (u.getNickname().equalsIgnoreCase(nickname)) {
	            return true;
	        }
	    }
	    return false;
	}
	
	public void agregarUsuario(Usuario nuevo) {
		String nicknameNuevo = nuevo.getNickname();
	    int i = 0;

	    while (i < usuarios.size() && 
	           usuarios.get(i).getNickname().compareToIgnoreCase(nicknameNuevo) < 0) {
	        i++;
	    }
	    this.usuarios.add(i, nuevo); // lo inserta en la posición correcta
	}

	
	public Usuario devuelveUsuario(String nickname) {
		for (Usuario u : usuarios) {
	        if (u.getNickname().equals(nickname)) {
	            return u;
	        }
	    }
	return null; 
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

	public ArrayList<String> getUsuarios() {
		ArrayList<String> DirectorioParaVista = new ArrayList<String>();
	    for (Usuario u :usuarios) {
	    	DirectorioParaVista.add(u.getNickname()+"    ["+u.getEstado()+"]");
	    }
	    return DirectorioParaVista;
	}
	
	
	//Este getDirectorioFormateado es para enviar
	//A traves del Gestor de Conexiones
	public String getDirectorioFormateado() {
	    StringBuilder sb = new StringBuilder();
	    sb.append("DIRECTORIO`");
	    sb.append(usuarios.size());

	    for (Usuario u: usuarios) {
	    	sb.append("`").append(u.getNickname());
	    	if(u.isConectado()) {
		    	sb.append("`").append("Online");
	    	}
	    	else {
		    	sb.append("`").append("Offline");   		
	    	}
	    }

	    return sb.toString();
	}

	public void NotificarDesconeccion(String nombre) {
		Usuario u = devuelveUsuario(nombre);
		u.setConectado(false);
		
		Servidor.getInstancia().ActualizaDirectoriosClientes();
		ControladorServidor.getInstance().ActualizarVistas();
	}

	public void NotificarConeccion(String nickname) {
		Usuario u = devuelveUsuario(nickname);
		u.setConectado(true);
		
		Servidor.getInstancia().ActualizaDirectoriosClientes();
		ControladorServidor.getInstance().ActualizarVistas();
	}
}
