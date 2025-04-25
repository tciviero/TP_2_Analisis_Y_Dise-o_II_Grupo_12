package modelo.usuario;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import modelo.Contacto.Contacto;
import modelo.Contacto.IActualizarMensajes;
import vista.INotificable;
import excepciones.NicknameYaRegistradoException;
import excepciones.VentanaCerradaSinSeleccionadosException;


public class Usuario implements IFuncionalidadUsuario {
	private static final String IP_SERVIDOR = "192.168.1.45";
	private static final int PUERTO_SERVIDOR = 1234;
	private int puerto;
	private String nickName,ip;
	private ArrayList<Contacto> contactos;
	private static Usuario instancia = null;
	private ServerSocket serverSocket;
	private ArrayList<INotificable> suscriptores;
	private boolean ejecutando = false;
	private Socket socket;
	private DataOutputStream out;
	private DataInputStream in;
	private CountDownLatch latchAgendamiento;
	
	private Usuario() {
		suscriptores = new ArrayList<INotificable>();
		this.contactos = new ArrayList<Contacto>();
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

		
	public static Usuario getInstancia() {
		if(instancia == null) {
			instancia = new Usuario();
		}
		return instancia;
	}
	
	@Override
	public void NuevoMensajeRecibido(String[] arrayMensaje) {
		String comando = arrayMensaje[0];
		System.out.println("mensaje que recibe: " + comando);
		if(!comando.equalsIgnoreCase("ping123")) { //este comando se usa para testear si esta conectado el usuario
			if(comando.equalsIgnoreCase("MENSAJE")) {
				String nick_emisor = arrayMensaje[1];
				System.out.println("nick emisor: " + nick_emisor + " nick receptor: " + this.nickName);
				String textoMensaje = arrayMensaje[2];
				Contacto contactoMensaje = this.getContacto(nick_emisor); 
				int numAuxiliarNombre = 1;
				if(contactoMensaje != null) {
					contactoMensaje.addMensaje(nick_emisor,textoMensaje, false); //revisar
				}else {
					while(NombreYaUsado(nick_emisor)) {
						nick_emisor = nick_emisor + "(" + numAuxiliarNombre + ")";
						numAuxiliarNombre++;
					}
					Contacto nuevoContacto  = new Contacto(nick_emisor);
					nuevoContacto.addMensaje(nick_emisor,textoMensaje, false);
					contactos.add(nuevoContacto);
				}
				EventoNuevoMensajeRecibido();
			} else if(comando.equalsIgnoreCase("AGENDAR_USUARIO")) {
				for (int i=1;i<arrayMensaje.length;i++) {
				    System.out.println("agenda al usuario: " + arrayMensaje[i]);
				    this.agregarContacto(arrayMensaje[i]);
				}
				if (latchAgendamiento != null)
			        latchAgendamiento.countDown(); // levanta la barrera
			} else if(comando.equalsIgnoreCase("VENTANA_CERRADA")) {
				if (latchAgendamiento != null)
			        latchAgendamiento.countDown(); // levanta la barrera
			}
		}
	}
	
	private void NuevoMensajeEnviado(IActualizarMensajes destinatario, String texto) {
		destinatario.addMensaje(nickName,texto, true);
	}

	@Override
	public void Envia(Contacto destinatario, String texto) throws IOException {
		String mensaje = "MENSAJE" + "`"+ nickName + "`" + texto + "`" + destinatario.getNickName();
		out.writeUTF(mensaje);
		System.out.println("mensaje enviado al servidor: " + mensaje);
		NuevoMensajeEnviado(destinatario, texto);
	}
	
	@Override
	public void Registrarse(String nickname) throws NicknameYaRegistradoException {
        this.nickName = nickname;
		try {
            socket = new Socket(IP_SERVIDOR, PUERTO_SERVIDOR);
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
            
            // manda a registrarse al servidor
            String mensaje = "REGISTRAR" + "`" + this.nickName;
            out.writeUTF(mensaje);
            String respuesta = in.readUTF(); // el servidor avisa si puedo registrarlo
            if(respuesta.equalsIgnoreCase("REGISTRO_OK")) { //ok
                new Thread(this::escucharMensajes).start();	// se pone a escuchar al servidor
            }else {
                System.out.println("nickname ya registrado");
            	throw new NicknameYaRegistradoException(nickname);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	@Override
	public void escucharMensajes() {
		System.out.println(this.nickName + " esta en escucha");
        try {
            while (true) {
                String mensaje = in.readUTF(); // emisor`mensaje
                System.out.println("mensaje en escucharMensajes: " + mensaje);
                String[] partes = mensaje.split("`");
                NuevoMensajeRecibido(partes);
            }
        } catch (IOException e) {
            System.out.println("Conexión cerrada.");
        }
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
		return contactos;
	}

	public boolean NombreYaUsado(String nombre) {
		for (Contacto c : contactos) {
            if (c.getNickName().equals(nombre)) {
                return true; 
            }
        }
        return false;
	}
	
	@Override
	public boolean esContacto(String nickname) {
        for (Contacto contacto : contactos) {
            if (contacto.getNickName().equalsIgnoreCase(nickname)) {
                return true;
            }
        }
        return false;
    }
	
	public void agregarContacto(String nickname) {
		if(!esContacto(nickname)) {
			Contacto contacto = new Contacto(nickname);
			this.contactos.add(contacto);
			System.out.println("contacto " + nickname + " agregado");
		}
	}
	
	public Contacto getContacto(String nickname) {
        for (Contacto contacto : contactos) {
            if (contacto.getNickName().equalsIgnoreCase(nickname)) {
                return contacto; // Se encuentra el contacto y se retorna
            }
        }
        return null; // Si no se encuentra el contacto, retorna null
    }
	
	@Override
	public void agendarContacto() throws VentanaCerradaSinSeleccionadosException,IOException {
		int cant_contactos_pre = contactos.size();
		String mensaje = "AGENDAR" + "`" + this.nickName;// aca le pide al servidor el directorio para agendar contactos
		System.out.println("manda usuario: " + mensaje);
		out.writeUTF(mensaje);
		latchAgendamiento = new CountDownLatch(1);
		try {
			latchAgendamiento.await(); // espera a que le pasen todos los usuarios para agendar antes de seguir con la ejecucion
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (cant_contactos_pre >= contactos.size()) // si no se agregaron contactos no actualiza las vistas
			throw new VentanaCerradaSinSeleccionadosException();
	}
	
}
