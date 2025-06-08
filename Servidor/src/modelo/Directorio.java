package modelo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;



public class Directorio {
	private HashMap<String,Boolean> estadoUsuarios; //nickname y true (conectado) / false (desconectado)
	
	public Directorio() {
		this.estadoUsuarios = new HashMap<String,Boolean>();
	}
	
	public boolean usuarioEstaConectado(String nickname) { //devuelve si esta conectado el usuario
		return this.estadoUsuarios.get(nickname);
	}

	public boolean contieneUsuario(String nickname) {
	    return this.estadoUsuarios.containsKey(nickname);
	}
	
	public void agregarUsuario(Usuario nuevo) {
	    this.estadoUsuarios.put(nuevo.getNickname(),true); //lo agrego como conectado
	}
	
	public void agregarUsuarioEstado(String nickname,String estado) {
		if(estado.equalsIgnoreCase("online")) {
			this.estadoUsuarios.put(nickname, true);
		}else
			this.estadoUsuarios.put(nickname,false);
		
	}

	public ArrayList<String> getUsuarios() {
		ArrayList<String> DirectorioParaVista = new ArrayList<String>();
		String mensaje;
		String usuario;
		for (Entry<String, Boolean> entry : this.estadoUsuarios.entrySet()) {
			int CantMensajes=0;
			usuario = entry.getKey();
		    boolean conectado = entry.getValue();
		    if(entry.getValue())
		    	mensaje = usuario + "  [online]";
		    else {
		    	CantMensajes= MensajesUsuario.getInstance().CantMensajesPendientes(usuario);
		    	mensaje = usuario + "  [offline]" + "     ["+ CantMensajes+"]mensajes pendientes";
		    	
		    }
		    DirectorioParaVista.add(mensaje);
		}
	    return DirectorioParaVista;
	}
	
	
	//Este getDirectorioFormateado es para enviar
	//A traves del Gestor de Conexiones
	public String getDirectorioFormateado() {
	    String directorioFormateado = "DIRECTORIO`" + this.estadoUsuarios.size();
	    for (Entry<String, Boolean> entry : this.estadoUsuarios.entrySet()) {
	    	directorioFormateado = directorioFormateado + "`" + entry.getKey();
		    boolean conectado = entry.getValue();
		    if(entry.getValue())
		    	directorioFormateado = directorioFormateado + "`online";
		    else
		    	directorioFormateado = directorioFormateado + "`offline";
		}

	    return directorioFormateado;
	}
	
	public String getDirectorioFormateadoConsulta(String filtro) {
		 StringBuilder sb = new StringBuilder("DIRECTORIO");
	        int cont = 0;
	        for (Entry<String, Boolean> e : this.estadoUsuarios.entrySet()) {
	            if (e.getKey().contains(filtro)) {
	                cont++;
	                if (cont == 10) break;
	            }
	        }
	        sb.append("`").append(cont);
	        
	        int añadidos = 0;
	        for (Entry<String, Boolean> e : this.estadoUsuarios.entrySet()) {
	            if (e.getKey().contains(filtro)) {
	                sb.append("`")
	                  .append(e.getKey())
	                  .append("`")
	                  .append(e.getValue() ? "online" : "offline");
	                if (++añadidos == 10) break;
	            }
	        }
	        
	        System.out.println(sb.toString());
	        return sb.toString();
	}

	public void NotificarDesconexion(String nickname) {
		this.estadoUsuarios.put(nickname, false);
		
		//Servidor.getInstancia().ActualizaDirectoriosClientes(); ACTUALIZAR
		//ControladorServidor.getInstance().ActualizarVistas();
	}

	public void NotificarConexion(String nickname) {
		this.estadoUsuarios.put(nickname, true);
		
		//Servidor.getInstancia().ActualizaDirectoriosClientes(); ACTUALIZAR
		//ControladorServidor.getInstance().ActualizarVistas();
	}
}
