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
import java.util.Map;

import controlador.ControladorServidor;

public class Servidor {
	private static Servidor instancia = null;
	private boolean conectado=false;
	private static final String IP_SERVIDOR = "192.168.1.45";
	private static final int PUERTO_SERVIDOR = 1234;
	private ServerSocket serverSocket;
	
	private static HashMap<String,Socket> SocketsDeUsuarios ;

	private Servidor() {
	}
	
	public static Servidor getInstancia() {
		if(instancia == null) {
			instancia = new Servidor();
			SocketsDeUsuarios=new HashMap<String,Socket>();
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
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
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
            			iniciarSesion(nombreUsuario,socket);
            			break;
                    case "MENSAJE":
                    	nombreUsuario = dataArray[1];
            			String Mensaje = dataArray[2];
            			String NicknameReceptor = dataArray[3];
            			System.out.println("ENVIANDO MENSAJE SERVIDOR");
            			enviarMensaje(nombreUsuario,Mensaje,NicknameReceptor);
            			break;
                    /* El servidor no aceptar "Agendar", le envia el directorio completo al cliente
                      	Y el cliente con el directorio elige a quien agendar.
                      	case "AGENDAR":
                      
                    	nombreUsuario = dataArray[1];
            			Directorio.getInstance().mostrarDirectorio(nombreUsuario,socket);
            			break;]*/
            		default:
            			System.out.println("Solicitud ("+SOLICITUD+") desconocida");
            			break;
                }
				System.out.println("Sale de aca?");
                //Cada vez que se recibe algun mensaje de lo que sea
                //Se actualiza la vista del servidor
                ControladorServidor.getInstance().ActualizarVistas();
            }

        } catch (IOException e) {
            String nombre=Servidor.getNickname(socket);
        	System.out.println("Cliente desconectado:"+nombre);
        	
            //Antes de avisar a todos que se desconecto, es necesario
        	//Quitarlo del hashmap del servidor para que no tire error
        	//Sale "Error al enviar al socket"
        	if(nombre!=null) {
        		SocketsDeUsuarios.remove(nombre);
        		Directorio.getInstance().NotificarDesconeccion(nombre);
        	}
            
            
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
		//Socket socket_receptor = Directorio.getInstance().devuelveSocketUsuario(nick_receptor);
		Socket socket_receptor = this.SocketsDeUsuarios.get(nick_receptor);
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
			System.out.println("registro de usuario: " + nickname);
			mensaje_enviar +=  "OK"+"`"+"Registro exitoso";
			//Hay un nuevo usuario y se deberia actualizar el directorio
			//tanto del servidor como de todos los clientes
			Servidor.SocketsDeUsuarios.put(nickname,socket);
			//Se agrega al hashmap<nickname,socket> de esta clase
			Directorio.getInstance().agregarUsuario(new Usuario(nickname));
			ActualizaDirectoriosClientes();
		}
		else {
			System.out.println("Error Usuario ya existente");
			mensaje_enviar += "Error"+ "`" +"Usuario ya existente";
		}
		out.writeUTF(mensaje_enviar);
	}
	
	public void ActualizaDirectoriosClientes() {
		for (Socket socket : SocketsDeUsuarios.values()) {
		    try {
		        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
		        out.writeUTF(Directorio.getInstance().getDirectorioFormateado());
		        out.flush(); 
		    } catch (IOException e) {
		        System.out.println("Error al enviar al socket: " + e.getMessage());
		    }
		}
	}

	private void iniciarSesion(String nickname, Socket socket) throws IOException {
		DataOutputStream out = new DataOutputStream(socket.getOutputStream());
		String mensaje_enviar = "RES-INICIO"+ "`";
		if(Directorio.getInstance().contieneUsuario(nickname)) {
			if (SocketsDeUsuarios.containsKey(nickname)) {
				SocketsDeUsuarios.remove(nickname);
			}
			SocketsDeUsuarios.put(nickname, socket);
			Directorio.getInstance().NotificarConeccion(nickname);
			mensaje_enviar +=  "OK"+"`"+"Inicio exitoso";
		}
		else {
			mensaje_enviar += "Error"+ "`" +"El usuario no existe";
		}
		out.writeUTF(mensaje_enviar);
	}
	
	public boolean isConectado() {
		return conectado;
	}
	
	public static String getNickname(Socket socket) {
	    for (Map.Entry<String, Socket> entry : SocketsDeUsuarios.entrySet()) {
	        if (entry.getValue().equals(socket)) {
	            return entry.getKey(); // Este es el nickname
	        }
	    }
	    return null; // si no se encuentra
	}

}
