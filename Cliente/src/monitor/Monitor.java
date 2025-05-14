package monitor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import modelo.usuario.Usuario;

public class Monitor {
	private static Monitor instance = null;
	private String IP_Servidor = "192.168.1.45";
	private static final int PUERTO_SERVIDOR_PRIMARIO = 1234;
	private static final int PUERTO_SERVIDOR_SECUNDARIO = 1235;
	private static int primario_actual = PUERTO_SERVIDOR_PRIMARIO;
	
	private Monitor() {
		
	}
	
	public static Monitor get_instance() {
		if(instance == null) {
			instance = new Monitor();
		}
		return instance;
	}
	
	public int cual_es_primario() { //devuelve el puerto primario
		Socket socket;
		try {
			socket = new Socket(IP_Servidor, PUERTO_SERVIDOR_PRIMARIO);
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			String mensajeRegistro = "ES_PRIMARIO";
			out.writeUTF(mensajeRegistro);
			DataInputStream in = new DataInputStream(socket.getInputStream());
			String respuesta_servidor = in.readUTF();
			//System.out.println("RESPUESTA: " + respuesta_servidor);
			if(respuesta_servidor.equalsIgnoreCase("PRIMARIO")) { 
				if(primario_actual != PUERTO_SERVIDOR_PRIMARIO)
					Usuario.getInstancia().cambiarConexion(IP_Servidor, PUERTO_SERVIDOR_PRIMARIO);
				return PUERTO_SERVIDOR_PRIMARIO;
			}else {
				Socket socket2 = new Socket(IP_Servidor,PUERTO_SERVIDOR_SECUNDARIO);
				if(primario_actual != PUERTO_SERVIDOR_SECUNDARIO)
					Usuario.getInstancia().cambiarConexion(IP_Servidor, PUERTO_SERVIDOR_SECUNDARIO);
				return PUERTO_SERVIDOR_SECUNDARIO;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return PUERTO_SERVIDOR_PRIMARIO;
	}
}
