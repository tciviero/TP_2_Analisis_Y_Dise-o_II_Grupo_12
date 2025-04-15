package modelo.usuario;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import modelo.Contacto.Contacto;
import modelo.Contacto.IActualizarMensajes;
import vista.INotificable;

public class Usuario implements IFuncionalidadUsuario {
	private int puerto;
	private String nickName,ip;
	private ArrayList<Contacto> contactos;
	private static Usuario instancia = null;
	private ServerSocket serverSocket;
	private ArrayList<INotificable> suscriptores;
	private boolean ejecutando = false;
	
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

		
	public static Usuario getInstancia() {
		if(instancia == null) {
			instancia = new Usuario();
		}
		return instancia;
	}
	
	public void Iniciar(String Nombre, String ip, int puerto) {
		this.puerto = puerto;
		this.ip=ip;
		this.nickName = Nombre;
		this.contactos = new ArrayList<Contacto>();
		Conectar();
	}
	
	@Override
	public void NuevoMensajeRecibido(String[] arrayMensaje) {
		String nombreMensaje = arrayMensaje[0];
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
	}
	
	private void NuevoMensajeEnviado(IActualizarMensajes destinatario, String texto) {
		destinatario.addMensaje(nickName,texto, true);
	}
	
	public void Conectar() {
		this.ejecutando = true;
		new Thread() {
			public void run() {
				try{
					serverSocket= new ServerSocket(puerto);
					while (ejecutando) {
						Socket socket = serverSocket.accept();
						DataInputStream in = new DataInputStream(socket.getInputStream());
						String data = in.readUTF();
						String[] dataArray = data.split("[`]");
						NuevoMensajeRecibido(dataArray);
						socket.close();
						in.close();
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}	
		}.start();
	}

	@Override
	public void Envia(Contacto destinatario, String texto) throws IOException {
		Socket clientSocket = new Socket();
		clientSocket.connect(new InetSocketAddress(destinatario.getIp(), destinatario.getPuerto()), 1000); //1s de timeout
		DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
		String mensaje = nickName + "`"+ ip + "`" + puerto +"`"+ texto;
		out.writeUTF(mensaje);
		clientSocket.close();
		NuevoMensajeEnviado(destinatario, texto);
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

	public boolean EsContacto(String iP_Receptor, int puerto_Receptor) {
		for (Contacto c : contactos) {
            if (c.getIp().equals(iP_Receptor) && c.getPuerto() == puerto_Receptor) {
                return true; // Se encontró el contacto con la misma IP y puerto
            }
        }
        return false; // No se encontró el contacto}
	}
	public Contacto getContacto(String ip, int puerto) {
        for (Contacto contacto : contactos) {
            if (contacto.getIp().equals(ip) && contacto.getPuerto() == puerto) {
                return contacto; // Se encuentra el contacto y se retorna
            }
        }
        return null; // Si no se encuentra el contacto, retorna null
    }
	

	@Override
	public void conectar(String nombre, String ip, int puerto) {
		Usuario.getInstancia().Iniciar(nombre, ip, puerto);
	}

	@Override
	public void agendarContacto(Contacto nuevoContacto) {
		if(!EsContacto(nuevoContacto.getIp(),nuevoContacto.getPuerto())) {
			contactos.add(nuevoContacto);
		}else { //actualizar nombre
			this.getContacto(nuevoContacto.getIp(),nuevoContacto.getPuerto()).setNickName(nuevoContacto.getNickName());
		}
	}
	
}
