package modelo.usuario;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import controlador.Controlador;
import excepciones.AgotoIntentosConectarException;
import excepciones.NoRespondePrimario;
import excepciones.UsuarioConSesionActivaException;
import excepciones.UsuarioNoRegistradoException;
import modelo.Conversacion;
import modelo.IActualizarMensajes;
import modelo.Contacto.Contacto;
import vista.INotificable;

public class Usuario implements IFuncionalidadUsuario {
	private static Usuario instancia = null;

	private final int puerto_monitor = 8888;
	private int puerto;
	private String nickName,ip;

	private ArrayList<Contacto> agenda;
	private ArrayList<Conversacion> conversaciones;
	private ArrayList<UsuarioYEstado> directorio;
	private int intentosConectar;
	private ArrayList<INotificable> suscriptores;
	
	public boolean estaConectado = false;

	private String ip_servidor;
	private int puerto_servidor;
	private Socket socket;	//con el socket se comunica con el servidor
	//private ServerSocket serverSocket;
	CountDownLatch latchConexion;
	
	private Usuario() {
		suscriptores = new ArrayList<INotificable>();
	}
	
	public void AgregarSuscriptor(INotificable nuevoSuscriptor) {
		suscriptores.add(nuevoSuscriptor);
	}
	
	public void QuitarSuscriptor(INotificable exSuscriptor) {
		suscriptores.remove(exSuscriptor);
	}
	
	private void EventoNuevoMensajeRecibido() {
		for (INotificable suscriptor: suscriptores) {
			suscriptor.OnNuevoMensajeRecibido();
		}
	}

	private void EventoNotificacionRecibido(String mensaje) {
		for (INotificable suscriptor: suscriptores) {
			suscriptor.OnNuevoNotificacion(mensaje);
		}
	}
	private void VistaConectado() {
		for (INotificable suscriptor: suscriptores) {
			suscriptor.conectado();
		}
	}
	
	public static Usuario getInstancia() {
		if(instancia == null) {
			instancia = new Usuario();
		}
		return instancia;
	}
	
	public void Iniciar(String Nombre, String ip, int puerto) {
		this.puerto = puerto;
		this.ip = ip;
		this.nickName = Nombre;
		this.agenda = new ArrayList<Contacto>();
		this.conversaciones = new ArrayList<Conversacion>();
		this.directorio = new ArrayList<UsuarioYEstado>();
	}
	
	public void iniciarSesion(String nickname) throws IOException, AgotoIntentosConectarException {
		this.Conectar();
        //this.socket.connect(new InetSocketAddress(this.ip, this.puerto_servidor), 1000);
		DataOutputStream out = new DataOutputStream(socket.getOutputStream());
		String mensaje_servidor = "INICIAR`" + nickname;
		out.writeUTF(mensaje_servidor);
	}
	
	public void Conectar() throws AgotoIntentosConectarException{
		this.latchConexion = new CountDownLatch(1);
		intentosConectar = 0;
	    while (true && intentosConectar < 5) {
	        try {
	        	InetAddress local = InetAddress.getLocalHost();
	            // Obtener nueva IP y puerto del monitor
	            obtenerNuevoServidorDesdeMonitor(); 

	            System.out.println("Conectando a servidor en " + local.getHostAddress() + " : " + this.puerto_servidor);
	            this.socket = new Socket();
	            socket.connect(new InetSocketAddress(local.getHostAddress(), this.puerto_servidor), 1000);

	            new Thread(() -> {
	            	EscucharMensajesServidor();
				}).start();
	            
	            /*String mensaje = "agregar_socket`"+this.nickName;
	            enviarMensaje(mensaje);
	            System.out.println("se manda agregar socket al servidor");*/
	             
	            break; // si todo salio bien, salgo del bucle
	        } catch (IOException e) {
	        	intentosConectar++;
	            System.err.println("Fallo la conexión al servidor, reintentando en 1 segundo...");
	            try {
	                Thread.sleep(1000);
	            } catch (InterruptedException ex) {
	                ex.printStackTrace();
	            }
	        }
	    }
	    if (intentosConectar >= 5) {
	    	//se agoto los intentos, tiro excepcion para avisar al usuario que no se pudo
	    	throw new AgotoIntentosConectarException();
	    }
	}
	
	public void esperarConexion() {
	    try {
	        this.latchConexion.await();
	    } catch (InterruptedException e) {
	        e.printStackTrace();
	    }
	}
	
	private final Object lockEnvio = new Object();

	private void enviarMensaje(String mensaje) {
	    synchronized (lockEnvio) {
	        try {
	            DataOutputStream out = new DataOutputStream(this.socket.getOutputStream());
	            out.writeUTF(mensaje);
	            out.flush();
	        } catch (IOException e) {
	            System.err.println("Error al enviar mensaje: " + e.getMessage());
	        }
	    }
	}
	
	/*private void enviarAgregarSocketAlServidor() {
	    String mensaje = "agregar_socket`" + this.nickName;
		try {
	        DataOutputStream out = new DataOutputStream(this.socket.getOutputStream());
	        out.writeUTF(mensaje);
	        out.flush();
	        System.out.println("se le envia al servidor");
	    } catch (IOException e) {
	        System.err.println("Error al enviar mensaje al servidor: " + e.getMessage());
	    }
	}*/
	
	public void enviarRequestRegistro() throws IOException {
		String mensaje = "registrar`"+this.nickName;
        enviarMensaje(mensaje);
		System.out.println("se envia registrar al servidor");
	}
	
	public void obtenerNuevoServidorDesdeMonitor() throws AgotoIntentosConectarException {
        try {
        	Socket socket = new Socket();
        	InetAddress local = InetAddress.getLocalHost();
			socket.connect(new InetSocketAddress(local.getHostAddress(), this.puerto_monitor), 1000);
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			String mensajeRegistro = "CUAL_PRIMARIO";
			out.writeUTF(mensajeRegistro);
			DataInputStream in = new DataInputStream(socket.getInputStream());
			String data = in.readUTF();
			System.out.println("El Monitor nos envio este mensaje: "+data);
			String[] dataArray = data.split("`");
			
			if(dataArray[1].equalsIgnoreCase("NO_HAY")) {
				throw new AgotoIntentosConectarException();
			}
			else {
				this.ip_servidor = dataArray[0];
				this.puerto_servidor = Integer.parseInt(dataArray[1]);
				
			}
			
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void ActualizaIPServidor(){
        try {
        	Socket socket = new Socket();
        	InetAddress local = InetAddress.getLocalHost();
			socket.connect(new InetSocketAddress(local.getHostAddress(), this.puerto_monitor), 1000);
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			String mensajeRegistro = "CUAL_PRIMARIO";
			out.writeUTF(mensajeRegistro);
			DataInputStream in = new DataInputStream(socket.getInputStream());
			String data = in.readUTF();
			System.out.println("El Monitor nos envio este mensaje: "+data);
			String[] dataArray = data.split("`");
			
			if(dataArray[1].equalsIgnoreCase("NO_HAY")) {
				
			}
			else {
				this.ip_servidor = dataArray[0];
				this.puerto_servidor = Integer.parseInt(dataArray[1]);
				
			}
			
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void EscucharMensajesServidor(){
		
		System.out.println("Dentro de un hilo conectado al servidor... esperando");
		//this.Conectado=true;

		DataInputStream in = null;
		try {
			in = new DataInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		 //socket.connect(new InetSocketAddress(this.ip, this.puerto_servidor), 1000);
		DataOutputStream out;
		
		while(true) {
			try {
				String data=null;
				data = in.readUTF();
				System.out.println("El servidor nos envio este mensaje: "+data);
				String[] dataArray = data.split("`");
				
				String respuesta = dataArray[0].toUpperCase();
				
				suich(respuesta,dataArray);
				
			} catch (IOException e) {
				//El servidor no responde se debe comunicar con el monitor
				System.out.println("El servidor ["+this.ip_servidor+":"+this.puerto_servidor+"] no responde esperando al monitor...");
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				
				ActualizaIPServidor();
				
				try {
					this.socket.close();
					this.socket = new Socket();
					this.socket.connect(new InetSocketAddress(this.ip_servidor, this.puerto_servidor), 1000);
					in = new DataInputStream(socket.getInputStream());
					
					String mensaje = "agregar_socket`"+this.nickName;
		            enviarMensaje(mensaje);
		            System.out.println("se manda agregar socket al servidor");
					
		            System.out.println("El servidor ["+this.ip_servidor+":"+this.puerto_servidor+"] es el nuevo servidor primario");
				} catch (IOException e1) {
					//Se genera socketTimeOutException pero solo repite el mensaje de arriba
					//Esperando al monitor...
				}
				
			}
		}

	}
	
	private void suich(String respuesta,String[] dataArray) {
		switch(respuesta) {
		case "RES-REGISTRO":
			if(dataArray[1].equalsIgnoreCase("OK")) {
				System.out.println("Usuario registrado exitosamente");
				this.estaConectado = true;
				EventoNotificacionRecibido(dataArray[2]);
				VistaConectado();
			}
			else {
				System.out.println("Error de registro:"+dataArray[2]);
				EventoNotificacionRecibido("Error de registro:"+dataArray[2]);
			}
			break;
		case "RES-INICIO":
			if(dataArray[1].equalsIgnoreCase("OK")) {
				System.out.println("Usuario Logueado exitosamente");
				this.estaConectado = true;
				EventoNotificacionRecibido(dataArray[2]);
				VistaConectado();
				if(dataArray.length>3) {
					int cant_mensajes_recibidos_desconectado = Integer.parseInt(dataArray[4]);
					if(cant_mensajes_recibidos_desconectado > 0) { //si tiene mensajes pendientes
						String emisor,mensaje;
						int aux = 5;
						for(int i=0;i<cant_mensajes_recibidos_desconectado;i++) {
							emisor = dataArray[aux];
							mensaje = dataArray[aux+1];
							aux += 2;
							System.out.println("emisor: " + emisor + " mensaje: " + mensaje);
							NuevoMensajeRecibido(emisor,mensaje);
						}
					}
				}
			}
			else {
				System.out.println("Error de Inicio:"+dataArray[2]);
				EventoNotificacionRecibido("Error de Inicio:"+dataArray[2]);
			}
			break;
		case "Res-envio":
			if(dataArray[1] == "OK") {
				//envio exitoso
			}
			else {
				//error envio
			}
			break;
		case "RECIBIR":
			String nicknameEmisor=dataArray[1];
			String mensaje=dataArray[2];
			NuevoMensajeRecibido(nicknameEmisor,mensaje);
			break;
		case "DIRECTORIO":
			//Llega la lista de contactos, que son solo strings con los nicknames
			int cantidadContactos = Integer.parseInt(dataArray[1]);
			this.directorio.clear();
			for (int i = 2; i < dataArray.length; i++) {
				String nickname=dataArray[i];
				i++;
				String estado= dataArray[i];
				System.out.println("usuario: " + nickname + estado);
				this.directorio.add(new UsuarioYEstado(nickname,estado));
			}
			EventoDirectorioRecibido();
			break;
		case "SOCKET_AGREGADO":
			System.out.println("se agrego el socket en el servidor");
			this.latchConexion.countDown();
			break;
		default:
			System.out.println("Respuesta ("+respuesta+") desconocida");
			break;
		}

		
	}

	/*public boolean isConectado() {
		return Conectado;
	}*/

	private void EventoDirectorioRecibido() {
		for (INotificable suscriptor: suscriptores) {
			suscriptor.ActualizarDirectorio(this.directorio);
		}
	}
	
	public void enviarRequestConsultaDirectorio(String nicknameConsulta) {
		try {
			//Socket socket = new Socket();
			//InetAddress local = InetAddress.getLocalHost();
			//socket.connect(new InetSocketAddress(local.getHostAddress(), this.puerto_servidor), 1000);
			DataOutputStream out = new DataOutputStream(this.socket.getOutputStream());
			String mensajeConsulta = "CONSULTA" + "`" + nickName + "`" + nicknameConsulta;
			System.out.println(nickName + " CONSULTA POR " + nicknameConsulta);
			out.writeUTF(mensajeConsulta);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void enviarRequestInicioSesion(String nickname) throws UsuarioConSesionActivaException, AgotoIntentosConectarException,UsuarioNoRegistradoException {
		
		obtenerNuevoServidorDesdeMonitor();
		this.socket = new Socket();
        try {
        	InetAddress local = InetAddress.getLocalHost();
			this.socket.connect(new InetSocketAddress(local.getHostAddress(), this.puerto_servidor), 1000);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			String mensajeRegistro = "COMPROBAR`" + nickname;
			out.writeUTF(mensajeRegistro);
			DataInputStream in = new DataInputStream(socket.getInputStream());
			String respuesta_servidor = in.readUTF();
			System.out.println("RESPUESTA: " + respuesta_servidor);
			if(respuesta_servidor.equalsIgnoreCase("INICIO_OK")) { //se pudo iniciar sesion
				//respuesta_servidor = in.readUTF();
				System.out.println("RESPUESTA: " + respuesta_servidor);
				System.out.println("inicio de sesion OK"); //esta todo ok
			}else if(respuesta_servidor.equalsIgnoreCase("YA_INICIADO")){
					throw new UsuarioConSesionActivaException(nickname);
				} else if(respuesta_servidor.equalsIgnoreCase("NO_REGISTRADO")) { //no fue registrado el usuario
					throw new UsuarioNoRegistradoException(nickname);
				}
					
			} catch (IOException e) { //server desconectado
				//Se debe crear una notificacion
				e.printStackTrace();
			}		
	}
	
	public void enviarRequestMensaje(String mensaje, String destinatario) {
		try {
			//Socket socket = new Socket();
			//InetAddress local = InetAddress.getLocalHost();
			//socket.connect(new InetSocketAddress(local.getHostAddress(), this.puerto_servidor), 1000);
			DataOutputStream out = new DataOutputStream(this.socket.getOutputStream());
			String mensajeRegistro = "Enviar" + "`" + nickName + "`" + mensaje + "`" + destinatario;
			out.writeUTF(mensajeRegistro);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void notificarDesconectado() {
		DataOutputStream out;
		try {
			//Socket socket = new Socket();
			//InetAddress local = InetAddress.getLocalHost();
			//socket.connect(new InetSocketAddress(local.getHostAddress(), this.puerto_servidor), 1000);
			out = new DataOutputStream(this.socket.getOutputStream());
			String mensaje = "DESCONEXION" + "`" + nickName;
			out.writeUTF(mensaje);
			System.out.println("mensaje enviado: " + mensaje);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean getEstaConectado() {
		return this.estaConectado;
	}
	
	@Override
	public void NuevoMensajeRecibido(String Emisor, String texto) {
		System.out.println("AAAAA: recibimos mensaje:"+texto);
		Conversacion c = getConversacion(Emisor);	//Buscamos la conversacion
		c.addMensaje(Emisor, texto, false);			//Agregamos el mensaje Ageno
		EventoNuevoMensajeRecibido();
		
	}
	
	private void NuevoMensajeEnviado(IActualizarMensajes destinatario, String texto) {
		destinatario.addMensaje(nickName,texto, true);
	}
	
	public String getNickName() {
		return nickName;
	}

	public int getPuerto() {
		return puerto;
	}

	public String getIp() {
		return ip;
	}

	public ArrayList<Contacto> getContactos() {
		return agenda;
	}
	
	public ArrayList<Conversacion> getConversaciones() {
		/*ArrayList<Conversacion> contactosConConversaciones = new ArrayList<Conversacion>();
		for(Conversacion c : conversaciones ) {
			if(c.getMensajes().size() > 0) {
				contactosConConversaciones.add(c);
			}
		return contactosConConversaciones;
		}*/
		return conversaciones;
	}
	


	@Override
	public void conectar(String nombre, String ip, int puerto) throws IOException {
		Iniciar(nombre, ip, puerto);
	}
//--- Conversaciones
	
	public Conversacion getConversacion(String nickname) {
		for (Conversacion c : conversaciones) {
	        if (c.getNickName().equals(nickname)) {
	            return c;
	        }
	    }
		Conversacion nueva = new Conversacion(nickname);
	    conversaciones.add(nueva);
	    return nueva;
	}
	

	public boolean ExisteConversacion(String nickname) {
		for (Conversacion c : conversaciones) {
	        if (c.getNickName().equals(nickname)) {
	            return true;
	        }
	    }
		return false;
	}
	
//----AGENDA--------------------
//----AGENDA-o-GestorDeContactos
//----AGENDA--------------------
	
	@Override
	public boolean EsContacto(String nickname) {
		for (Contacto c : agenda) {
			if (c.getNickName().equalsIgnoreCase(nickname)) {
				return true; 
			}
		}
		return false; 
	}
	
	public Contacto getContacto(String nickname) {
		for (Contacto contacto : agenda) {
			if (contacto.getNickName().equalsIgnoreCase(nickname)) {
				return contacto; 
			}
		}
		return null; 
	}
	public void agendarContacto(Contacto usuario) {
		//Agrega a la agenda alfabeticamente
		if(!EsContacto(usuario.getNickName())) {
			int i = 0;
	        while (i < agenda.size() && 
	               agenda.get(i).getNickName().compareToIgnoreCase(usuario.getNickName()) < 0) {
	            i++;
	        }
	        agenda.add(i, usuario);
		}
	}
	@Override
	public void agendarContacto(String nickname) {
		agendarContacto(new Contacto(nickname));
	}


	
}