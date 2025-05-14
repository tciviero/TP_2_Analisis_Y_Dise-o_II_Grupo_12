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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import controlador.ControladorServidor;
import exception.PuertoYaUsadoException;

public class Servidor {
	private ServerSocket serverSocket;
	private static MensajesUsuario mensajesUsuario;
	private static HashMap<String,Socket> SocketsDeUsuarios;
	private boolean esPrimario;
	private String IP_Servidor = "192.168.1.45";
	int puertoPropio,otroPuerto;
	
	public Servidor(int puertoPropio, int otroPuerto) {
		SocketsDeUsuarios = new HashMap<String,Socket>();
		mensajesUsuario = new MensajesUsuario();
		this.puertoPropio = puertoPropio;
		this.otroPuerto = otroPuerto;
	}

	/*public void Iniciar() throws PuertoYaUsadoException {
		try {
	        serverSocket = new ServerSocket(puertoPropio);
	    } catch (IOException e) {
	        throw new PuertoYaUsadoException();
	    }

	    // Hilo para aceptar conexiones de clientes y heartbeats
	    new Thread(() -> {
	        System.out.println("Servidor escuchando conexiones en puerto " + puertoPropio);
	        while (true) {
	            try {
	                Socket socket = serverSocket.accept();
	                System.out.println("Nueva conexión desde " + socket.getInetAddress());
	                new Thread(() -> manejarCliente(socket)).start();
	            } catch (IOException e) {
	                System.err.println("Error aceptando conexión: " + e.getMessage());
	            }
	        }
	    }).start();

	    // Ahora que ya estamos escuchando, podemos verificar el rol
	    try {
	        Thread.sleep(500); // opcional: pequeña pausa para permitir al otro iniciar
	    } catch (InterruptedException e) {
	        Thread.currentThread().interrupt();
	    }

	    if (otroServidorActivo()) {
	        esPrimario = false;
	        System.out.println("Iniciando como SECUNDARIO");
	        monitorearPrimario();
	    } else {
	        esPrimario = true;
	        System.out.println("Iniciando como PRIMARIO");
	        //enviarHeartbeats();
	    }
	}*/
	
	public void Iniciar() throws PuertoYaUsadoException {
	    try {
	        serverSocket = new ServerSocket(puertoPropio);  // Inicia el servidor en el puerto deseado
	    } catch (IOException e) {
	        throw new PuertoYaUsadoException();  // Lanza una excepción si el puerto ya está en uso
	    }

	    System.out.println("Servidor escuchando conexiones en puerto " + puertoPropio);

	    // Bucle principal del servidor, que acepta y maneja conexiones secuencialmente
	    while (true) {
	        try {
	            Socket socket = serverSocket.accept();  // Espera y acepta una conexión entrante
	            System.out.println("Nueva conexión desde " + socket.getInetAddress());

	            // Aquí manejas la conexión de forma secuencial, sin crear hilos
	            manejarCliente(socket);  // Procesa la conexión en el mismo hilo

	        } catch (IOException e) {
	            System.err.println("Error aceptando conexión: " + e.getMessage());
	            break;  // Si hay un error al aceptar, se sale del bucle
	        }
	    }
	}
	
	 private boolean otroServidorActivo() {
	        try (Socket socket = new Socket(IP_Servidor, otroPuerto)) {
	        	DataOutputStream out = new DataOutputStream(socket.getOutputStream());
	        	out.writeUTF("PING");
	            return true;
	        } catch (IOException e) {
	        	return false;
	        }
	 }
	 
	 /*private void enviarHeartbeats() {
		    new Thread(() -> {
		        while (esPrimario) {
		            try (Socket socket = new Socket(IP_Servidor, otroPuerto);
		                 DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {

		                out.writeUTF("HEARTBEAT");

		            } catch (IOException e) {
		                System.err.println("No se pudo enviar el heartbeat al secundario: " + e.getMessage());
		            }

		            try {
		                Thread.sleep(1000); // esperar 1 segundo antes de enviar el próximo
		            } catch (InterruptedException e) {
		                Thread.currentThread().interrupt(); // buena práctica
		                break;
		            }
		        }
		    }).start();
	}*/
	 
	 private void monitorearPrimario() {
	        new Thread(() -> {
	            while (true) {
	                boolean activo = otroServidorActivo();
	                if (!activo) {
	                    System.out.println("Primario inactivo. Asumiendo rol PRIMARIO.");
	                    esPrimario = true;
	                    //enviarHeartbeats();
	                    break;
	                }
	                try {
	                    Thread.sleep(2000);
	                } catch (InterruptedException e) { }
	            }
	        }).start();
	    }
	
	/*private void manejarCliente(Socket socket) {
       
		try (DataInputStream in = new DataInputStream(socket.getInputStream())) {
	        String data = in.readUTF();
	        System.out.println("Se recibió un mensaje de un cliente: " + data);
	        String[] dataArray = data.split("`");

	        String SOLICITUD = dataArray[0].toUpperCase();
	        System.out.println("SOLICITUD RECIBIDA: " + SOLICITUD);
	        
	        String nombreUsuario = null;
	
                switch (SOLICITUD) {
                    case "REGISTRAR":
                    	nombreUsuario = dataArray[1];
            			registrar(nombreUsuario,socket);
                        break;
                    case "COMPROBAR":
                    	nombreUsuario = dataArray[1];
                    	comprobarUsuarioSesion(nombreUsuario,socket);
            			break;
                    case "INICIAR":
                    	nombreUsuario = dataArray[1];
                    	iniciarSesion(nombreUsuario,socket);
            			break;
                    case "ENVIAR":
                    	nombreUsuario = dataArray[1];
            			String Mensaje = dataArray[2];
            			String NicknameReceptor = dataArray[3];
            			System.out.println("ENVIANDO MENSAJE SERVIDOR");
            			enviarMensaje(nombreUsuario,Mensaje,NicknameReceptor);
            			break;
                    case "PING":
                    	System.out.println("llega ping");
                    	break;
                    case "ES_PRIMARIO":
                    	DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                    	String respuesta;
                    	if(esPrimario)
                    		respuesta = "primario";
                    	else
                    		respuesta = "secundario";
                    	out.writeUTF(respuesta);
                    	break;
            		default:
            			System.out.println("Solicitud ("+SOLICITUD+") desconocida");
            			break;
                }
                //Cada vez que se recibe algun mensaje de lo que sea
                //Se actualiza la vista del servidor
                ControladorServidor.getInstance().ActualizarVistas();
            } catch (IOException e) { //se desconecta el usuario
            e.printStackTrace();
        	String nombre = Servidor.getNickname(socket);
        	System.out.println("Cliente desconectado:"+nombre);
        	
            //Antes de avisar a todos que se desconecto, es necesario
        	//Quitarlo del hashmap del servidor para que no tire error
        	//Sale "Error al enviar al socket"
        	if(nombre!=null) {
        		SocketsDeUsuarios.remove(nombre);
        		Directorio.getInstance().NotificarDesconexion(nombre);
        	}
           }
       
    }*/
	
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
	            case "PING":
	                System.out.println("llega ping");
	                break;
	            case "ES_PRIMARIO":
	                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
	                String respuesta;
	                if (esPrimario)
	                    respuesta = "primario";
	                else
	                    respuesta = "secundario";
	                out.writeUTF(respuesta);
	                break;
	            default:
	                System.out.println("Solicitud (" + SOLICITUD + ") desconocida");
	                break;
	        }

	        // Se actualiza la vista del servidor después de procesar la solicitud
	        ControladorServidor.getInstance().ActualizarVistas();

	    } catch (IOException e) {
	        e.printStackTrace();
	        String nombre = Servidor.getNickname(socket);
	        System.out.println("Cliente desconectado: " + nombre);
	        
	        // Si el cliente se desconectó inesperadamente, quitamos el socket de la lista
	        if (nombre != null) {
	            SocketsDeUsuarios.remove(nombre);
	            Directorio.getInstance().NotificarDesconexion(nombre);
	        }
	    } finally {
	        // Aquí puedes manejar el cierre de los recursos si es necesario
	        try {
	            if (in != null) {
	                in.close();
	            }
	            // socket.close(); // Solo cerrar el socket si realmente lo deseas
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	}

	private void enviarMensaje(String nick_emisor, String mensaje, String nick_receptor) throws IOException {
		System.out.println(nick_emisor + " Desea enviar a [" + nick_receptor+ "] el siguiente: -" + mensaje+"-");
		Socket socket_receptor = getSocket(nick_receptor);
				
		if(Directorio.getInstance().usuarioEstaConectado(nick_receptor)) { //si esta conectado lo envia
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
		if(!Directorio.getInstance().contieneUsuario(nickname)) { //nickname no esta registrado
			System.out.println("registro de usuario: " + nickname);
			mensaje_enviar +=  "OK"+"`"+"Registro exitoso";
			//Hay un nuevo usuario y se deberia actualizar el directorio
			//tanto del servidor como de todos los clientes
			Servidor.SocketsDeUsuarios.put(nickname,socket);
			//Se agrega al hashmap<nickname,socket> de esta clase
			Directorio.getInstance().agregarUsuario(new Usuario(nickname));
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
		        out.writeUTF(Directorio.getInstance().getDirectorioFormateado());
		        out.flush(); 
		    } catch (IOException e) {
		        System.out.println("Error al enviar al socket: " + e.getMessage());
		    }
		}
	}
	
	private void iniciarSesion(String nickname, Socket socket) throws IOException {
		String mensaje_enviar = "RES-INICIO`OK`Inicio exitoso";
		SocketsDeUsuarios.put(nickname, socket);
		Directorio.getInstance().NotificarConexion(nickname);
		DataOutputStream out = new DataOutputStream(socket.getOutputStream());
		System.out.println("se inicia sesion" + nickname);
		
		mensajesUsuario.mostrarMensajes(); //aca hay que agarrar los mensajes de nickname y mandarselos para que cargue la vista
		
		mensaje_enviar += "`" + mensajesUsuario.historial_mensajes_recibidos(nickname);
		
		out.writeUTF(mensaje_enviar);
	}

	private void comprobarUsuarioSesion(String nickname, Socket socket) throws IOException {
		DataOutputStream out = new DataOutputStream(socket.getOutputStream());
		String mensaje_enviar;
		if(Directorio.getInstance().contieneUsuario(nickname)) { //si primero se registro ok, si no error de que el nickname no se registro
			if(Directorio.getInstance().usuarioEstaConectado(nickname)) { //si esta conectado error ! no puede iniciar sesion 
				mensaje_enviar = "YA_INICIADO";
			}else { //inicio sesion
				System.out.println("ESTA TODO OK EN COMPROBAR USUARIO");
				SocketsDeUsuarios.put(nickname, socket);
				Directorio.getInstance().NotificarConexion(nickname);
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
