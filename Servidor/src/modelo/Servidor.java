package modelo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.BindException;
import java.net.ConnectException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import controlador.ControladorServidor;
import exception.PuertoYaUsadoException;
//import modelo.usuario.UsuarioYEstado;
import exception.SecundarioCaidoException;

public class Servidor {
	//private ServerSocket serverSocket;
	private MensajesUsuario mensajesUsuario;
	private static HashMap<String,Socket> SocketsDeUsuarios;
	private ArrayList<Solicitud> solicitudesActuales;
	
	private Directorio directorio;
	
	private String ipPropio;
	private String IP_Monitor;
	private String IP_Secundario;
	
	private int puertoPropio, puertoMonitor,puertoSecundario;
	
	private boolean soyPrimario;
	
	private int SolicitudID=0;
	private boolean SeDebeSincronizar=false;
	
	public Servidor(String ipPropio,int puertoPropio, String ipMonitor,int puertoMonitor) {
		SocketsDeUsuarios = new HashMap<String,Socket>();
		mensajesUsuario = MensajesUsuario.getInstance();
		solicitudesActuales = new ArrayList<Solicitud>();
		this.ipPropio = ipPropio;
		this.puertoPropio = puertoPropio;
		this.IP_Monitor = ipMonitor;
		this.puertoMonitor = puertoMonitor;
		this.soyPrimario = false;
		this.directorio = new Directorio();
		
		this.SolicitudID=0;
		this.SeDebeSincronizar=false;
		
		
		String ipcomun = crearIP();
		this.ipPropio = ipcomun;
		this.IP_Monitor =ipcomun;
		
	}
	
    public void iniciar() throws PuertoYaUsadoException {
		try {
			InetAddress direccion = InetAddress.getByName(ipPropio);
            ServerSocket serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(direccion, puertoPropio));
            serverSocket.close();
            informarAlMonitor();
            iniciarEscuchaClientes();	//escucha en puerto propio
            if (soyPrimario) {
            	System.out.println("pasa a ser primario");
                //escucharPingsDelMonitor(); //Ahora escucha al monitor en el puerto propio
            }
		} catch (IOException e) {
			System.out.println("El metodo Servidor.iniciar() tira exception :"+e.getMessage());
			throw new PuertoYaUsadoException();
		}
    }

    private void informarAlMonitor() {
    	System.out.println("ip:puertomonitor"+IP_Monitor+":"+puertoMonitor);
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

    	//Responde PING ECHO en el ip puerto comun del servidor.
/*    private void escucharPingsDelMonitor() {
        new Thread(() -> {
            try{ // puerto reservado solo para pings
            	InetAddress direccion = InetAddress.getByName(ipPropio);
                ServerSocket serverPing = new ServerSocket();
                serverPing.bind(new InetSocketAddress(direccion, puertoMonitor));
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
    }*/ 
    
    private void iniciarEscuchaClientes() {
        new Thread(() -> {
            try{
            	InetAddress direccion = InetAddress.getByName(ipPropio);
                ServerSocket serverSocket = new ServerSocket();
                serverSocket.bind(new InetSocketAddress(direccion, puertoPropio));
                
                System.out.println("Servidor escuchando en " + ipPropio + " " + puertoPropio);
                
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
	    DataOutputStream out =null;
	    try {
	        in = new DataInputStream(socket.getInputStream());
	        out = new DataOutputStream(socket.getOutputStream());
	        String data = in.readUTF();
	        
	        
	        
	        String[] dataArray = data.split("`");
	        String SOLICITUD = dataArray[0].toUpperCase();
	        
	        
	        if(!SOLICITUD.contains("PING")) {
	        	System.out.println("Se recibió una solicitud: " + SOLICITUD);
	        	String nombreUsuario = null;
		        switch (SOLICITUD) {//Solicitudes de usuario
		            case "REGISTRAR":
		            	this.SolicitudID=this.SolicitudID+1;
		            	EnviarSolicitudRecibidaAlSecundario(this.SolicitudID,data); //"ATENDIENDO"
		            	
		                nombreUsuario = dataArray[1];
		                registrar(nombreUsuario, socket);
		                ControladorServidor.getInstance().ActualizarVistas(this.directorio.getUsuarios());
		                ActualizarEstadoSolicitudAlSecundario(this.SolicitudID);//"ATENDIDA"
		                EnviarDirectorioYMensajesASecundario();
		                break;
		            case "COMPROBAR":
		            	this.SolicitudID=this.SolicitudID+1;
		            	EnviarSolicitudRecibidaAlSecundario(this.SolicitudID,data); //"ATENDIENDO"
		            	
		            	nombreUsuario = dataArray[1];
		                comprobarUsuarioSesion(nombreUsuario, socket);
		                
		                ControladorServidor.getInstance().ActualizarVistas(this.directorio.getUsuarios());
		                ActualizarEstadoSolicitudAlSecundario(this.SolicitudID);//"ATENDIDA"
		                EnviarDirectorioYMensajesASecundario();
		                break;
		            case "INICIAR":
		            	this.SolicitudID=this.SolicitudID+1;
		            	EnviarSolicitudRecibidaAlSecundario(this.SolicitudID,data); //"ATENDIENDO"
		                
		            	nombreUsuario = dataArray[1];
		                iniciarSesion(nombreUsuario, socket);
		                
		                ControladorServidor.getInstance().ActualizarVistas(this.directorio.getUsuarios());
		                ActualizarEstadoSolicitudAlSecundario(this.SolicitudID);//"ATENDIDA"
		                EnviarDirectorioYMensajesASecundario();
		                break;
		            case "ENVIAR":
		            	this.SolicitudID=this.SolicitudID+1;
		            	EnviarSolicitudRecibidaAlSecundario(this.SolicitudID,data); //"ATENDIENDO"
		                
		            	nombreUsuario = dataArray[1];
		                String Mensaje = dataArray[2];
		                String NicknameReceptor = dataArray[3];
		                System.out.println("ENVIANDO MENSAJE SERVIDOR");
		                enviarMensaje(nombreUsuario, Mensaje, NicknameReceptor);
		                
		                ControladorServidor.getInstance().ActualizarVistas(this.directorio.getUsuarios());
		                ActualizarEstadoSolicitudAlSecundario(this.SolicitudID);//"ATENDIDA"
		                EnviarDirectorioYMensajesASecundario();
		                break;
		            case "DESCONEXION":
		            	this.SolicitudID=this.SolicitudID+1;
		            	EnviarSolicitudRecibidaAlSecundario(this.SolicitudID,data); //"ATENDIENDO"

		            	String nickname = dataArray[1];
		            	System.out.println("Cliente desconectado: " + nickname);
		    	        // Si el cliente se desconectó inesperadamente, quitamos el socket de la lista
		    	        if (nickname != null) {
		    	            SocketsDeUsuarios.remove(nickname);
		    	            this.directorio.NotificarDesconexion(nickname);
		    	            ControladorServidor.getInstance().ActualizarVistas(this.directorio.getUsuarios());
		    	        }
		                
		    	        ActualizarEstadoSolicitudAlSecundario(this.SolicitudID);//"ATENDIDA"
		    	        EnviarDirectorioYMensajesASecundario();
		                break;
		            case "DIRECTORIO":
		            	System.out.println("Se recibió el directorio");
		            	//Llega la lista de contactos, que son solo strings con los nicknames
						int cantidadContactos = Integer.parseInt(dataArray[1]);
						this.directorio = new Directorio();
						for (int i = 2; i < dataArray.length; i++) {
							String nombre = dataArray[i];
							i++;
							String estado = dataArray[i];
							//System.out.println("usuario: " + nombre + " " + estado);
							this.directorio.agregarUsuarioEstado(nombre, estado);
						}
						ControladorServidor.getInstance().ActualizarVistas(this.directorio.getUsuarios());
		            	break;
		            	
		            case "SOS_PRIMARIO":
		                //escucharPingsDelMonitor();
		                this.soyPrimario = true;
		                ControladorServidor.getInstance().ActualizarRolVista("Primario");
		                break;
		            case "SINCRONIZAR": //SINCRONIZAR`IP`PUERTO
		            	this.SeDebeSincronizar=true;
		            	
		            	this.IP_Secundario = dataArray[1];
		            	this.puertoSecundario = Integer.parseInt(dataArray[2]);
		            	System.out.println("sincronizar con: " + this.IP_Secundario + " " + this.puertoSecundario);
		            	EnviarDirectorioYMensajesASecundario();
		            	break;
		            case "ID_SOL":
		            	AlmacenaSolicitud(Integer.parseInt(dataArray[1]),dataArray[2],dataArray);
		            	//Se almacenan las solicitudes más recientes del servidor primario
		            	//Por si se cae el primario saber que era lo que estaba haciendo
		            	break;
		            case "MENSAJES_PENDIENTES":
		            	System.out.println("Se recibieron mensajes pendientes");
		            	this.mensajesUsuario.CargarHashMap(data);
		            	//this.mensajesUsuario.mostrarMensajes();
		            	ControladorServidor.getInstance().ActualizarVistas(this.directorio.getUsuarios());
		            	break;
		            case "CONSULTA":
		            	System.out.println("Se pide buscar nickname y devolver resultados");
		            	DevolverResultadosBusquedaNickname(dataArray[1],dataArray[2]);
		            	break;
		            default:
		            	System.out.println("Solicitud (" + SOLICITUD + ") desconocida");
		            	break;
		        }
		        
		        // Se actualiza la vista del servidor después de procesar la solicitud
		        ControladorServidor.getInstance().ActualizarVistas(this.directorio.getUsuarios());
	        }else {
    			out.writeUTF("ECHO");
	        }

	    }catch(SecundarioCaidoException e) {
	    	if(this.soyPrimario) {
				this.SeDebeSincronizar=false;
				this.IP_Secundario = "";
				this.puertoSecundario = 0;
			}
	    }
	    catch (IOException e) {
	    	InetAddress remoteAddress = socket.getInetAddress();
            int remotePort = socket.getPort();
            
            InetAddress localAddress = socket.getLocalAddress();
            int localPort = socket.getLocalPort();
	    	System.out.println("Se deconecto IP:puerto"+remoteAddress.getHostAddress()+":"+remotePort);
	    	
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

//---------Esto lo ejecuta el servidor cuando es secundario ------------
	private void AlmacenaSolicitud(int idsol,String estado ,String dataArray[]) {    	
    	if(estado.equalsIgnoreCase("ATENDIENDO")) {
    		int i=3;
    		String solicitudPrimario="";	//La solicitud que el servidor primario esta atendiendo ahora
    		
    		while (i<dataArray.length) {
    			solicitudPrimario=solicitudPrimario+"`"+dataArray[i];
    			i++;
    		}
    		
    		this.solicitudesActuales.add(new Solicitud(idsol,true,solicitudPrimario));
    	}
    	else {
    		if (idsol >= 0 && idsol < this.solicitudesActuales.size()) {
    		    Solicitud Aeliminar = this.solicitudesActuales.get(idsol);
    		    Aeliminar.setAtendiento(false);
    		    this.solicitudesActuales.remove(idsol);
    		}
    		
    	}
    	System.out.println("UltimaSolicitud ["+idsol+"] esta "+estado);
	}

//------------COMUNICACION CON SERVIDOR SECUNDARIO------------
	private void ActualizarEstadoSolicitudAlSecundario(int solicitudID) throws SecundarioCaidoException {
		String mensaje = "ID_SOL"+"`"+solicitudID+"`"+"ATENDIDA";
		try {
			Socket socket_secundario = new Socket(this.IP_Secundario, this.puertoSecundario);
			DataOutputStream out_secundario = new DataOutputStream(socket_secundario.getOutputStream());
			out_secundario.writeUTF(mensaje);
		} catch (IOException e) {
			if(e instanceof java.net.ConnectException) {
				//Intento de comunicación con el secundario
				throw new SecundarioCaidoException();
			}
		}
	}

	private void EnviarSolicitudRecibidaAlSecundario(int solicitudID, String data) throws SecundarioCaidoException {
		String mensaje = "ID_SOL"+"`"+solicitudID+"`"+"ATENDIENDO"+"`"+data;
		try {
			Socket socket_secundario = new Socket(this.IP_Secundario, this.puertoSecundario);
			DataOutputStream out_secundario = new DataOutputStream(socket_secundario.getOutputStream());
			out_secundario.writeUTF(mensaje);
		} catch (IOException e) {
			if(e instanceof java.net.ConnectException) {
				//Intento de comunicación con el secundario
				throw new SecundarioCaidoException();
			}
		}
	}

	private void EnviarDirectorioYMensajesASecundario() throws SecundarioCaidoException {
		if(SeDebeSincronizar) {
			try {
				Socket socket;
				//---DIRECTORIO---
				Socket socket_secundario = new Socket(this.IP_Secundario, this.puertoSecundario);
				DataOutputStream out_secundario = new DataOutputStream(socket_secundario.getOutputStream());
				
				String todo_el_directorio = this.directorio.getDirectorioFormateado();
				out_secundario.writeUTF(todo_el_directorio); 
				
				//---MENSAJES-PENDIENTES---
				socket_secundario = new Socket(this.IP_Secundario, this.puertoSecundario);	//tuve que poner esto denuevo
				out_secundario = new DataOutputStream(socket_secundario.getOutputStream()); //Porque no llegaban los mensajes pendientes
				
				String todo_Mensajes_Pendientes = this.mensajesUsuario.getTodosMensajesFormateado();
				out_secundario.writeUTF(todo_Mensajes_Pendientes);
			}catch (IOException e) {
				if(e instanceof java.net.ConnectException) {
					//Intento de comunicación con el secundario
					throw new SecundarioCaidoException();
				}
			}
		}
	}

//------------COMUNICACION CON CLIENTES------------
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
			Servidor.SocketsDeUsuarios.put(nickname,socket);
			//Se agrega al hashmap<nickname,socket> de esta clase
			this.directorio.agregarUsuario(new Usuario(nickname));
			//ActualizaDirectoriosClientes(); NO SE USA MAS, NO SE HACE ASI
			System.out.println("mensaje enviado: " + mensaje_enviar);
		}
		else {
			System.out.println("Error Usuario ya existente");
			mensaje_enviar += "Error"+ "`" +"Usuario ya existente";
		}
		out.writeUTF(mensaje_enviar);
		out.flush();
	}
	
	public void DevolverResultadosBusquedaNickname(String nickname, String nicknameConsulta) {
		Socket socket = SocketsDeUsuarios.get(nickname);
		if(socket != null) {
			try {
				DataOutputStream out = new DataOutputStream(socket.getOutputStream());
				out.writeUTF(this.directorio.getDirectorioFormateadoConsulta(nicknameConsulta));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	/* NO SE USA MAS, CADA CLIENTE PREGUNTA SOBRE CIERTO NICKNAME Y DEVUELVE PRIMEROS 10 RESULTADOS
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
	*/
	private void iniciarSesion(String nickname, Socket socket) throws IOException {
		String mensaje_enviar = "RES-INICIO`OK`Inicio exitoso";
		SocketsDeUsuarios.put(nickname, socket);
		this.directorio.NotificarConexion(nickname);
		ControladorServidor.getInstance().ActualizarVistas(this.directorio.getUsuarios());
		DataOutputStream out = new DataOutputStream(socket.getOutputStream());
		System.out.println("se inicia sesion" + nickname);
		
		//Muestra todos los mensajes que pendiente de entregar.
		mensajesUsuario.mostrarMensajes(); //aca hay que agarrar los mensajes de nickname y mandarselos para que cargue la vista
		
		
		String mensaje = mensajesUsuario.historial_mensajes_recibidos(nickname);
		// mensaje tiene una estructura similar a esto: 
		//mensaje= "HISTORIAL`cantMensajes`emisor`mensaje`...." 
		//lo tengo que sacar del historial
		mensajesUsuario.eliminarMensajesYaLeidos(nickname);
		if(!mensaje.equalsIgnoreCase("no_tuvo")) {
			//Se le envian al cliente el historial con el formato mostrado arriba
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
	
	
    public static String crearIP() {
		InetAddress addr = null;
		try {
			Enumeration <NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			while(interfaces.hasMoreElements()) {
				NetworkInterface iface= interfaces.nextElement();
				if(!iface.isUp() || iface.isLoopback()) continue;
				
				Enumeration<InetAddress> addresses = iface.getInetAddresses();
				while (addresses.hasMoreElements()) {
					addr = addresses.nextElement();
					if (addr instanceof Inet4Address && !addr.isLoopbackAddress()) {
						//System.out.println("IP privada real: " + addr.getHostAddress());
					}
				}
			}
			return addr.getHostAddress();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getIP() {
		return this.ipPropio;
	}

	public int getPuerto() {
		return this.puertoPropio;
	}

	public boolean esPrimario() {
		return this.soyPrimario;
	}

}
