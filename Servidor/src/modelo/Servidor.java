package modelo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.BindException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import controlador.ControladorServidor;
import exception.PuertoYaUsadoException;
import modelo.usuario.UsuarioYEstado;

public class Servidor {
	//private ServerSocket serverSocket;
	private MensajesUsuario mensajesUsuario;
	private static HashMap<String,Socket> SocketsDeUsuarios;
	private String ipPropio;
	private String IP_Monitor;
	private final int puertoPING = 9999;
	private int puertoPropio, puertoMonitor;
	private boolean soyPrimario;
	private Directorio directorio;
	
	public Servidor(String ipPropio,int puertoPropio, String ipMonitor,int puertoMonitor) {
		SocketsDeUsuarios = new HashMap<String,Socket>();
		mensajesUsuario = new MensajesUsuario();
		this.ipPropio = ipPropio;
		this.puertoPropio = puertoPropio;
		this.IP_Monitor = ipMonitor;
		this.puertoMonitor = puertoMonitor;
		this.soyPrimario = false;
		this.directorio = new Directorio();
	}
	
    public void iniciar() throws PuertoYaUsadoException {
		try {
			InetAddress direccion = InetAddress.getByName(ipPropio);
            ServerSocket serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(direccion, puertoPropio));
            serverSocket.close();
            informarAlMonitor();
            iniciarEscuchaClientes();
            if (soyPrimario) {
            	System.out.println("pasa a ser primario");
                escucharPingsDelMonitor();
            }
		} catch (IOException e) {
			throw new PuertoYaUsadoException();
		}
    }

    private void informarAlMonitor() {
        try (Socket socket = new Socket(IP_Monitor, puertoMonitor)) {
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            out.writeUTF("servidor_conectado`"+this.ipPropio+"`"+puertoPropio);

            DataInputStream in = new DataInputStream(socket.getInputStream());
            String respuesta = in.readUTF();
            System.out.println("El monitor respondió: " + respuesta);
            soyPrimario = respuesta.equalsIgnoreCase("sos_primario");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void escucharPingsDelMonitor() {
        new Thread(() -> {
            try{ // puerto reservado solo para pings
            	InetAddress direccion = InetAddress.getByName(ipPropio);
                ServerSocket serverPing = new ServerSocket();
                serverPing.bind(new InetSocketAddress(direccion, puertoPING));
            	while (true) {
                    Socket socket = serverPing.accept();
                    DataInputStream in = new DataInputStream(socket.getInputStream());
                    String mensaje = in.readUTF();
                    if (mensaje.equalsIgnoreCase("PING")) {
                    	System.out.println("LLEGA PING");
                        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                        out.writeUTF("ECHO");
                    }
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    private void iniciarEscuchaClientes() {
        new Thread(() -> {
            try{
            	InetAddress direccion = InetAddress.getByName(ipPropio);
                ServerSocket serverSocket = new ServerSocket();
                serverSocket.bind(new InetSocketAddress(direccion, puertoPropio));
                
                System.out.println("Servidor escuchando clientes en " + ipPropio + " " + puertoPropio);
                while (true) {
                    Socket cliente = serverSocket.accept();
                    new Thread(() -> manejarCliente(cliente)).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
	
	private void manejarCliente(Socket socket) {
	    DataInputStream in = null;
	    try {
	        in = new DataInputStream(socket.getInputStream());
	        String data = in.readUTF();
	        System.out.println("Se recibió un mensaje de un cliente: " + data);
	        String[] dataArray = data.split("`");

	        String SOLICITUD = dataArray[0].toUpperCase();
	        System.out.println("SOLICITUD RECIBIDA: " + SOLICITUD);
	        
	        String nombreUsuario = null;

	        switch (SOLICITUD) {
	            case "REGISTRAR":
	                nombreUsuario = dataArray[1];
	                registrar(nombreUsuario, socket);
	                break;
	            case "COMPROBAR":
	                nombreUsuario = dataArray[1];
	                comprobarUsuarioSesion(nombreUsuario, socket);
	                break;
	            case "INICIAR":
	                nombreUsuario = dataArray[1];
	                iniciarSesion(nombreUsuario, socket);
	                break;
	            case "ENVIAR":
	                nombreUsuario = dataArray[1];
	                String Mensaje = dataArray[2];
	                String NicknameReceptor = dataArray[3];
	                System.out.println("ENVIANDO MENSAJE SERVIDOR");
	                enviarMensaje(nombreUsuario, Mensaje, NicknameReceptor);
	                break;
	            case "SOS_PRIMARIO":
	                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
	                escucharPingsDelMonitor();
	                this.soyPrimario = true;
	                break;
	            case "SINCRONIZAR": //SINCRONIZAR`IP`PUERTO
	            	String ip_secundario = dataArray[1];
	            	int puerto_secundario = Integer.parseInt(dataArray[2]);
	            	System.out.println("sincronizar con: " + ip_secundario + " " + puerto_secundario);
	            	sincronizar(ip_secundario,puerto_secundario);
	            	break;
	            case "DESCONEXION":
	            	String nickname = dataArray[1];
	            	System.out.println("Cliente desconectado: " + nickname);
	    	        // Si el cliente se desconectó inesperadamente, quitamos el socket de la lista
	    	        if (nickname != null) {
	    	            SocketsDeUsuarios.remove(nickname);
	    	            this.directorio.NotificarDesconexion(nickname);
	    	            ControladorServidor.getInstance().ActualizarVistas(this.directorio.getUsuarios());
	    	        }
	            	break;
	            case "DIRECTORIO":
	            	//Llega la lista de contactos, que son solo strings con los nicknames
					int cantidadContactos = Integer.parseInt(dataArray[1]);
					this.directorio = new Directorio();
					for (int i = 2; i < dataArray.length; i++) {
						String nombre = dataArray[i];
						i++;
						String estado = dataArray[i];
						System.out.println("usuario: " + nombre + " " + estado);
						this.directorio.agregarUsuarioEstado(nombre, estado);
					}
					ControladorServidor.getInstance().ActualizarVistas(this.directorio.getUsuarios());
	            	break;
	            default:
	                System.out.println("Solicitud (" + SOLICITUD + ") desconocida");
	                break;
	        }
	        // Se actualiza la vista del servidor después de procesar la solicitud
	        ControladorServidor.getInstance().ActualizarVistas(this.directorio.getUsuarios());

	    } catch (IOException e) {
	        e.printStackTrace();
	        /*String nombre = Servidor.getNickname(socket);
	        System.out.println("Cliente desconectado: " + nombre);
	        
	        // Si el cliente se desconectó inesperadamente, quitamos el socket de la lista
	        if (nombre != null) {
	            SocketsDeUsuarios.remove(nombre);
	            this.directorio.NotificarDesconexion(nombre);
	            ControladorServidor.getInstance().ActualizarVistas(this.directorio.getUsuarios());
	        }*/
	    }
	}
	
	private void sincronizar(String ip_secundario, int puerto_secundario) {
		Socket socket;
		try {
			//ServerSocket serverSocket = new ServerSocket(puerto_secundario);
			//socket = new Socket(ip_secundario, puerto_secundario); va este en realidad
			Socket socket_secundario = new Socket(ip_secundario, puerto_secundario);
			DataOutputStream out_secundario = new DataOutputStream(socket_secundario.getOutputStream());
			//out.writeUTF("servidor_conectado`"+this.ipPropio+"`"+puertoPropio);
			//mandar todo el directorio
			String todo_el_directorio = this.directorio.getDirectorioFormateado();
			out_secundario.writeUTF(todo_el_directorio); //tiene que saber recibir el otro servidor
			//mandar todos los mensajes
			
			//out.writeUTF("servidor_conectado`"+ip_secundario+"`"+puerto_secundario);
			
		}catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void enviarMensaje(String nick_emisor, String mensaje, String nick_receptor) throws IOException {
		System.out.println(nick_emisor + " Desea enviar a [" + nick_receptor+ "] el siguiente: -" + mensaje+"-");
		Socket socket_receptor = getSocket(nick_receptor);
				
		if(this.directorio.usuarioEstaConectado(nick_receptor)) { //si esta conectado lo envia
			DataOutputStream out = new DataOutputStream(socket_receptor.getOutputStream());
			String mensaje_enviar = "RECIBIR" + "`" + nick_emisor + "`" + mensaje;
			System.out.println("mensaje a enviar al usuario desde el servidor: " + mensaje_enviar);
			out.flush();
			out.writeUTF(mensaje_enviar);
		} else {
			System.out.println("usuario receptor: " + nick_receptor + " esta desconectado");
			mensajesUsuario.agregarMensaje(nick_receptor, nick_emisor, mensaje); //lo guarda igual en el historial
		}
	}

	//Se fija si ya esta existe el usuario en el directorio
	//Si no existe lo agrega, y le envia a
	private void registrar(String nickname,Socket socket) throws IOException {
		DataOutputStream out = new DataOutputStream(socket.getOutputStream());
		String mensaje_enviar = "RES-REGISTRO"+ "`";
		if(!this.directorio.contieneUsuario(nickname)) { //nickname no esta registrado
			System.out.println("registro de usuario: " + nickname);
			mensaje_enviar +=  "OK"+"`"+"Registro exitoso";
			//Hay un nuevo usuario y se deberia actualizar el directorio
			//tanto del servidor como de todos los clientes
			Servidor.SocketsDeUsuarios.put(nickname,socket);
			//Se agrega al hashmap<nickname,socket> de esta clase
			this.directorio.agregarUsuario(new Usuario(nickname));
			ActualizaDirectoriosClientes();
			System.out.println("mensaje enviado: " + mensaje_enviar);
		}
		else {
			System.out.println("Error Usuario ya existente");
			mensaje_enviar += "Error"+ "`" +"Usuario ya existente";
		}
		out.writeUTF(mensaje_enviar);
		out.flush();
	}
	
	public void ActualizaDirectoriosClientes() {
		for (Socket socket : SocketsDeUsuarios.values()) {
		    try {
		        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
		        out.writeUTF(this.directorio.getDirectorioFormateado());
		        out.flush(); 
		    } catch (IOException e) {
		        System.out.println("Error al enviar al socket: " + e.getMessage());
		    }
		}
	}
	
	private void iniciarSesion(String nickname, Socket socket) throws IOException {
		String mensaje_enviar = "RES-INICIO`OK`Inicio exitoso";
		SocketsDeUsuarios.put(nickname, socket);
		this.directorio.NotificarConexion(nickname);
		ControladorServidor.getInstance().ActualizarVistas(this.directorio.getUsuarios());
		DataOutputStream out = new DataOutputStream(socket.getOutputStream());
		System.out.println("se inicia sesion" + nickname);
		
		mensajesUsuario.mostrarMensajes(); //aca hay que agarrar los mensajes de nickname y mandarselos para que cargue la vista
		
		String mensaje = mensajesUsuario.historial_mensajes_recibidos(nickname);
		//lo tengo que sacar del historial
		mensajesUsuario.eliminarMensajesYaLeidos(nickname);
		if(!mensaje.equalsIgnoreCase("no_tuvo")) {
			mensaje_enviar += "`" + mensaje;
			out.writeUTF(mensaje_enviar);
		}
	}

	private void comprobarUsuarioSesion(String nickname, Socket socket) throws IOException {
		DataOutputStream out = new DataOutputStream(socket.getOutputStream());
		String mensaje_enviar;
		if(this.directorio.contieneUsuario(nickname)) { //si primero se registro ok, si no error de que el nickname no se registro
			if(this.directorio.usuarioEstaConectado(nickname)) { //si esta conectado error ! no puede iniciar sesion 
				mensaje_enviar = "YA_INICIADO";
			}else { //inicio sesion
				System.out.println("ESTA TODO OK EN COMPROBAR USUARIO");
				SocketsDeUsuarios.put(nickname, socket);
				this.directorio.NotificarConexion(nickname);
				ControladorServidor.getInstance().ActualizarVistas(this.directorio.getUsuarios());
				mensaje_enviar = "INICIO_OK";
			}
		}else {
			mensaje_enviar = "NO_REGISTRADO";
		}
		out.writeUTF(mensaje_enviar);
	}
	
	public static String getNickname(Socket socket) {
	    for (Map.Entry<String, Socket> entry : SocketsDeUsuarios.entrySet()) {
	        if (entry.getValue().equals(socket)) {
	            return entry.getKey(); 
	        }
	    }
	    return null; 
	}
	public static Socket getSocket(String nickname) {
		for (Map.Entry<String, Socket> entry : SocketsDeUsuarios.entrySet()) {
	        if (entry.getKey().equals(nickname)) {
	            return entry.getValue(); 
	        }
	    }
	    return null;
	}

}
