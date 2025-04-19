package modelo;

import java.io.DataInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Servidor {
	private static Servidor instancia = null;
	private boolean conectado=false;
	private String ip;
	private int puerto;
	private ServerSocket serverSocket;

	private HashMap<String,Socket> usuarios= new HashMap<String,Socket>();

	private Servidor() {
		
	}
	
	public static Servidor getInstancia() {
		if(instancia == null) {
			instancia = new Servidor();
		}
		return instancia;
	}

	public void Iniciar(String Ip_Servidor, int Puerto_Servidor) {
		this.ip=Ip_Servidor;
		this.puerto=Puerto_Servidor;
		this.conectado=true;
		new Thread() {
			public void run() {
				try{
					serverSocket= new ServerSocket(puerto);
					while (conectado) {
						Socket socket = serverSocket.accept();
						DataInputStream in = new DataInputStream(socket.getInputStream());
						String data = in.readUTF();
						String[] dataArray = data.split("[`]");
						DerivaSolicitud(dataArray,socket);
						//socket.close();//Creo que ahora no lo tengo que cerrar
						//in.close();//Ni al socket ni al in
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}	
		}.start();
	}
	
	private void DerivaSolicitud(String[] solicitudRecibida,Socket socket) {
		String SOLICITUD = solicitudRecibida[0].toUpperCase();
		String NombreUsuario = null;
		
		switch(SOLICITUD) {
		case "REGISTRAR":
			NombreUsuario = solicitudRecibida[1];
			registrar(NombreUsuario,socket);
			break;
		case "INICIAR":
			NombreUsuario = solicitudRecibida[1];
			iniciarSesion(NombreUsuario);
			break;
		case "MENSAJE":
			NombreUsuario = solicitudRecibida[1];
			String Mensaje = solicitudRecibida[2];
			String NicknameReceptor = solicitudRecibida[3];
			enviarMensaje(NombreUsuario,Mensaje,NicknameReceptor);
			break;
		default:
			System.out.println("Solicitud ("+SOLICITUD+") desconocida");
			break;
		}
	}

	private void enviarMensaje(String nombreUsuario, String mensaje, String nicknameReceptor) {
		System.out.println(nombreUsuario + " Desea enviar a [" + nicknameReceptor+ "] el siguiente: -" + mensaje+"-");
	}

	private void registrar(String nickname,Socket socket) {
		if(this.usuarios.containsKey(nickname)) {
			//El usuario ya esta registrado EXCEPTION
		}
		else {
			//Usuario registrado con exito
			this.usuarios.put(nickname, socket);
		}
	}
	
	private void iniciarSesion(String nickname) {
		
	}

	public String getIp() {
		return ip;
	}
	public int getPuerto() {
		return puerto;
	}

	public boolean isConectado() {
		return conectado;
	}

}
