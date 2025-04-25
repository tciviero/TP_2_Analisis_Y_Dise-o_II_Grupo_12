package modelo;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import vista.VentanaDirectorio;

public class Directorio {
	private HashMap<String,Socket> usuarios = new HashMap<String,Socket>();
	private VentanaDirectorio ventana = new VentanaDirectorio();
	
	public boolean contieneUsuario(String nickname) {
		return this.usuarios.containsKey(nickname);
	}
	
	public void agregarUsuario(String nickname, Socket socket) {
		this.usuarios.put(nickname, socket);
	}
	
	public Socket devuelveSocketUsuario(String nickname) {
		return this.usuarios.get(nickname);
	}
	
	public void mostrarDirectorio(String usuarioSolicitante,Socket socket_solicitante) {
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
	}
	
}
