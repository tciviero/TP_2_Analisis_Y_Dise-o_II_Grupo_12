package modelo.usuario;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import modelo.Conversacion;
import modelo.IActualizarMensajes;
import modelo.Contacto.Contacto;
import vista.INotificable;

public class Usuario implements IFuncionalidadUsuario {
	private static Usuario instancia = null;

	private int puerto;
	private String nickName,ip;

	private ArrayList<Contacto> agenda;
	private ArrayList<Conversacion> conversaciones;
	private ArrayList<UsuarioYEstado> directorio;
	
	private ArrayList<INotificable> suscriptores;
	
	private boolean Conectado = false;

	private final int PUERTO_SERVIDOR = 1234;
	private Socket socket;	//con el socket se comunica con el servidor
	private ServerSocket serverSocket;
	
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
	
	public void Conectar() throws IOException {
		socket = new Socket();
		socket.connect(new InetSocketAddress(ip, PUERTO_SERVIDOR), 1000);
		new Thread(() -> {
			EscucharMensajesServidor(socket);
		}).start();
	}
	
	private void EscucharMensajesServidor(Socket socket){
		try {
			System.out.println("Dentro de un hilo conectado al servidor... esperando");
			this.Conectado=true;
			DataInputStream in = new DataInputStream(socket.getInputStream());
			while(true) {
				String data = in.readUTF();
				System.out.println("El servidor nos envio este mensaje: "+data);
				String[] dataArray = data.split("`");
				
				String respuesta = dataArray[0].toUpperCase();
				
				switch(respuesta) {
				case "RES-REGISTRO":
					if(dataArray[1].equalsIgnoreCase("OK")) {
						System.out.println("Usuario registrado exitosamente");
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
						EventoNotificacionRecibido(dataArray[2]);
						VistaConectado();
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
				case "DIRECTORIO":
					//Llega la lista de contactos, que son solo strings con los nicknames
					int cantidadContactos = Integer.parseInt(dataArray[1]);
					this.directorio.clear();
					for (int i = 2; i < dataArray.length; i++) {
						String nickname=dataArray[i];
						i++;
						String estado= dataArray[i];
						this.directorio.add(new UsuarioYEstado(nickname,estado));
					}
					EventoDirectorioRecibido();
					break;
				default:
					System.out.println("Respuesta ("+respuesta+") desconocida");
					break;
				}
			}
		} catch (IOException e) {
			this.Conectado=false;
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean isConectado() {
		return Conectado;
	}

	private void EventoDirectorioRecibido() {
		for (INotificable suscriptor: suscriptores) {
			suscriptor.ActualizarDirectorio(this.directorio);
		}
	}

	public void enviarRequestRegistro() throws IOException {
		if(!socket.isClosed()) {
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			String mensajeRegistro = "Registrar" + "`" + nickName;
			out.writeUTF(mensajeRegistro);
			System.out.println("Se envia al servidor:"+mensajeRegistro);
		}
	}
	
	public void enviarRequestInicioSesion() {
		if(!socket.isClosed()) {
			DataOutputStream out;
			try {
				out = new DataOutputStream(socket.getOutputStream());
				String mensajeRegistro = "Iniciar" + "`" + nickName;
				out.writeUTF(mensajeRegistro);
			} catch (IOException e) {
				//Se debe crear una notificacion
				e.printStackTrace();
			}
		}
	}
	
	private void enviarRequestMensaje(String mensaje, String destinatario) throws IOException {
		if(!socket.isClosed()) {
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			String mensajeRegistro = "Enviar" + "`" + nickName + "`" + mensaje + "`" + destinatario;
			out.writeUTF(mensajeRegistro);
		}
	}
	
	
	@Override
	public void NuevoMensajeRecibido(String[] arrayMensaje) {
/*		String nombreMensaje = arrayMensaje[0];
		if(!nombreMensaje.equalsIgnoreCase("ping123")) { //este nombre se usa para testear si esta conectado el usuario
			String ipMensaje = arrayMensaje[1];
			int puertoMensaje = Integer.parseInt(arrayMensaje[2]);
			String textoMensaje = arrayMensaje[3];
			Contacto contactoMensaje = getContacto(ipMensaje, puertoMensaje);
			int numAuxiliarNombre = 1;
			String nuevoNombre = nombreMensaje;
			if(contactoMensaje != null) {
				contactoMensaje.addMensaje(nombreMensaje,textoMensaje, false);
			}
			else {
				while(NombreYaUsado(nuevoNombre)) {
					nuevoNombre = nombreMensaje + "(" + numAuxiliarNombre + ")";
					numAuxiliarNombre++;
				}
				Contacto nuevoContacto  = new Contacto(nuevoNombre, ipMensaje, puertoMensaje);
				nuevoContacto.addMensaje(nombreMensaje,textoMensaje, false);
				contactos.add(nuevoContacto);
			}
			EventoNuevoMensajeRecibido();	
		}
		*/
	}
	
	private void NuevoMensajeEnviado(IActualizarMensajes destinatario, String texto) {
		destinatario.addMensaje(nickName,texto, true);
	}
	
	

	@Override
	public void Envia(Contacto destinatario, String texto) throws IOException {
/*		Socket clientSocket = new Socket();
		clientSocket.connect(new InetSocketAddress(destinatario.getIp(), destinatario.getPuerto()), 1000); //1s de timeout
		DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
		String mensaje = nickName + "`"+ ip + "`" + puerto +"`"+ texto;
		out.writeUTF(mensaje);
		clientSocket.close();
		NuevoMensajeEnviado(destinatario, texto);
*/
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
