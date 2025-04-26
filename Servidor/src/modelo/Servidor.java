package modelo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashMap;

import controlador.ControladorServidor;

public class Servidor {
	private static Servidor instancia = null;
	private boolean conectado=false;
	private static final String IP_SERVIDOR = "192.168.1.45";
	private static final int PUERTO_SERVIDOR = 1234;
	private ServerSocket serverSocket;
	
	private Servidor() {
	}
	
	public static Servidor getInstancia() {
		if(instancia == null) {
			instancia = new Servidor();
		}
		return instancia;
	}
	
	public void Iniciar() {
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(PUERTO_SERVIDOR);
                System.out.println("Servidor iniciado esperando nuevas conexiones en puerto " + PUERTO_SERVIDOR);

                while (true) {
                    Socket socket = serverSocket.accept();
                 	System.out.println("Nuevo cliente conectado desde " + socket.getInetAddress());

                    // crea el hilo para las solicitudes
                    new Thread(() -> manejarCliente(socket)).start();
                    System.out.println("Estamos fuera del hilo que atiende al cliente");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        System.out.println("Estamos fuera del hilo que acepta nuevas conexiones");
    }
	
	private void manejarCliente(Socket socket) {
        try {
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            String nombreUsuario = null;

            while (true) {
            	String data = in.readUTF();
                System.out.println("Se recibio un mensaje de un cliente:"+data);
                String[] dataArray = data.split("`");

                String SOLICITUD = dataArray[0].toUpperCase();
                System.out.println("SOLICITUD RECIBIDA: " + SOLICITUD);

                switch (SOLICITUD) {
                    case "REGISTRAR":
                    	nombreUsuario = dataArray[1];
            			registrar(nombreUsuario,socket);
                        break;
                    case "INICIAR":
                    	nombreUsuario = dataArray[1];
            			iniciarSesion(nombreUsuario);
            			break;
                    case "MENSAJE":
                    	nombreUsuario = dataArray[1];
            			String Mensaje = dataArray[2];
            			String NicknameReceptor = dataArray[3];
            			System.out.println("ENVIANDO MENSAJE SERVIDOR");
            			enviarMensaje(nombreUsuario,Mensaje,NicknameReceptor);
            			break;
                    case "AGENDAR":
                    	nombreUsuario = dataArray[1];
            			Directorio.getInstance().mostrarDirectorio(nombreUsuario,socket);
            			break;
            		default:
            			System.out.println("Solicitud ("+SOLICITUD+") desconocida");
            			break;
                }
                //Cada vez que se recibe algun mensaje de lo que sea
                //Se actualiza la vista del servidor
                ControladorServidor.getInstance().ActualizarVistas();
            }

        } catch (IOException e) {
            System.out.println("Cliente desconectado.");
            
        } finally {
            try {
                if (socket != null) socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

	private void enviarMensaje(String nick_emisor, String mensaje, String nick_receptor) throws IOException {
		System.out.println(nick_emisor + " Desea enviar a [" + nick_receptor+ "] el siguiente: -" + mensaje+"-");
		Socket socket_receptor = Directorio.getInstance().devuelveSocketUsuario(nick_receptor);
		if(!socket_receptor.isClosed()) {
			DataOutputStream out = new DataOutputStream(socket_receptor.getOutputStream());
			String mensaje_enviar = "MENSAJE" + "`" + nick_emisor + "`" + mensaje;
			System.out.println("mensaje a enviar al usuario desde el servidor: " + mensaje_enviar);
			out.flush();
			out.writeUTF(mensaje_enviar);
		}else
			System.out.println("SOCKET CERRADO");
	}

	//Se fija si ya esta existe el usuario en el directorio
	//Si no existe lo agrega, y le envia a
	private void registrar(String nickname,Socket socket) throws IOException {
		DataOutputStream out = new DataOutputStream(socket.getOutputStream());
		String mensaje_enviar = "RES-REGISTRO"+ "`";
		if(!Directorio.getInstance().contieneUsuario(nickname)) { //nickname no esta registrado
			Directorio.getInstance().agregarUsuario(nickname, socket);
			System.out.println("registro de usuario: " + nickname);
			mensaje_enviar +=  "OK"+"`"+"Registro exitoso";
		}
		else {
			System.out.println("Error Usuario ya existente");
			mensaje_enviar += "Error"+ "`" +"Usuario ya existente";
		}
		out.writeUTF(mensaje_enviar);
		//Tambien hay que enviarla el directorio
		out.writeUTF(Directorio.getInstance().getDirectorioFormateado());
	}
	
	private void iniciarSesion(String nickname) {
		
	}
	
	public boolean isConectado() {
		return conectado;
	}

}
