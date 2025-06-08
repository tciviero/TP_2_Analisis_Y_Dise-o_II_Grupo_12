package controlador;

import java.awt.event.ActionEvent;
import modelo.usuario.MetodoPersistenciaUsuarios;
import vista.IVista;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.BindException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import excepciones.AgotoIntentosConectarException;
import excepciones.NoRespondePrimario;
import excepciones.UsuarioConSesionActivaException;
import excepciones.UsuarioNoRegistradoException;
import modelo.Conversacion;
import modelo.IVerConversacion;
import modelo.Contacto.Contacto;
import modelo.usuario.IFuncionalidadUsuario;
import modelo.usuario.Usuario;
import modelo.usuario.UsuarioYEstado;
import vista.VentanaChat;

import vista.VistaMetodoPersistencia;

public class Controlador implements ActionListener, ListSelectionListener{
	IFuncionalidadUsuario usuario = Usuario.getInstancia();
	private IVista vista;
	private static Controlador instance = null;
	
	private VistaMetodoPersistencia vistaMetodoPersistencia = new VistaMetodoPersistencia();
	
	private String IP_Usuario = null;
	
	private Controlador() {
		
	}
	
	public static Controlador getInstance() {
		if(instance == null)
			instance = new Controlador();
		return instance;
	}

	public void Iniciar() {
		IP_Usuario = crearIP();
		vista = new VentanaChat(IP_Usuario);
		vista.addActionListener(this);
		vista.addListSelectionListener(this);
		Usuario.getInstancia().AgregarSuscriptor(vista);
	}
	
	public void notificarDesconectado() {
		Usuario.getInstancia().notificarDesconectado();
	}
	
	
	public IVista getVista(){
		return this.vista;
	}
	
	
	public void UsuarioExistente(String ip, int puerto) throws IOException {
		try(Socket socket = new Socket()){
			socket.connect(new InetSocketAddress(ip, puerto), 100);
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			String mensaje = "ping123`";
			out.writeUTF(mensaje);
			socket.close();
		}
	}
	
	public String crearIP() {
		InetAddress addr = null;
		try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();

                if (!iface.isUp() || iface.isLoopback()) continue;

                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    addr = addresses.nextElement();
                    if (addr instanceof Inet4Address && !addr.isLoopbackAddress()) {
                        System.out.println("IP privada real: " + addr.getHostAddress());
                    }
                }
            }
            return addr.getHostAddress();
		} catch (Exception e) {
            e.printStackTrace();
        }
		return null;
	}
	

	private void registrar() {
		String nombre = this.vista.getNickNameUsuarioText();
		try {
			String metodo = this.vistaMetodoPersistencia.pedirMetodoPersistencia();
			MetodoPersistenciaUsuarios.get_instance().guardarUsuarioEnArchivo(nombre, metodo);
			System.out.println("metodo elegido: " + metodo);
			//if(!Usuario.getInstancia().isConectado()) {
			Usuario.getInstancia().Iniciar(nombre, IP_Usuario);
			Usuario.getInstancia().Conectar();
			//Usuario.getInstancia().esperarConexion();
			
			Usuario.getInstancia().setearMetodoPersistencia(metodo);
			//}
			Usuario.getInstancia().enviarRequestRegistro();
		} catch (AgotoIntentosConectarException e) {
			this.vista.onFalloConectarServidor();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}

	private void comprobarInicioSesion() {
		String nombre = this.vista.getNickNameUsuarioText();
		try {
			MetodoPersistenciaUsuarios.get_instance().cargarDesdeArchivo();
			String metodo_seleccionado = MetodoPersistenciaUsuarios.get_instance().buscarMetodoUsuario(nombre);
			System.out.println("metodo que eligio: " + metodo_seleccionado);
			Usuario.getInstancia().setearMetodoPersistencia(metodo_seleccionado);
			
			Usuario.getInstancia().Iniciar(nombre, IP_Usuario);
			Usuario.getInstancia().enviarRequestInicioSesion(nombre); //aca ya comprobo todo
			//ahora toca iniciar sesion
			Usuario.getInstancia().iniciarSesion(nombre);
		} catch (AgotoIntentosConectarException e) {
			this.vista.onFalloConectarServidor();
		} catch (UsuarioConSesionActivaException e) {
			this.vista.onFalloUsuarioConSesionActiva(nombre);
		} catch (UsuarioNoRegistradoException e) {
			this.vista.onFalloUsuarioNoRegistrado(nombre);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void agendar() {
		UsuarioYEstado usuario = vista.getUsuarioSeleccionado();
		System.out.println("Se desea agendar a"+usuario.getNickname());
		Usuario.getInstancia().agendarContacto(usuario.getNickname());
		vista.ActualizaListaContactos();
		this.vista.volverAChat();
		this.vista.onUsuarioAgendadoExitosamente();
	}
	
	private void hablar() {
		Contacto seleccionado = vista.getContactoSeleccionado();
		Conversacion aAbrir = Usuario.getInstancia().getConversacion(seleccionado.getNickName());
		this.vista.volverAChat();
		vista.CargarChat(aAbrir);
		vista.ActualizarListaConversaciones();
	}

	private void enviar() {
		String msg = vista.getTecladoText();
		if(!msg.equalsIgnoreCase("")) {
			Conversacion actual= vista.getConversacionAbierta();
			if(actual!=null) {
				Usuario.getInstancia().enviarRequestMensaje(msg, actual.getNickName(),"AES");
				actual.addMensaje(Usuario.getInstancia().getNickName(), msg, true);
				actual.SetCantidadMensajesSinLeer(0);
				vista.setTecladoText("");
				vista.CargarChat(vista.getConversacionAbierta());
			}
		}
	}
	
	public void buscarUsuarios() {
		this.vista.buscarUsuarios();
	}
	
	public void verContactos() {
		this.vista.verContactos();
	}
	
	public void volverAChat() {
		this.vista.volverAChat();
	}
	
	public void buscarNickname() {
		String nickname = vista.getSearchText();
		if(!nickname.equalsIgnoreCase("")) {
			Usuario.getInstancia().enviarRequestConsultaDirectorio(nickname);
		}
	}
		
	@Override
	public void actionPerformed(ActionEvent e) {
		String comando = e.getActionCommand();
		System.out.println("Se apreto "+comando);
		if(comando.equalsIgnoreCase("REGISTRAR")) {
			registrar();
		}
		else if(comando.equalsIgnoreCase("INICIAR")) {
			comprobarInicioSesion();
		}
		else if(comando.equalsIgnoreCase("AGENDAR")) {
			agendar();
		}
		else if(comando.equalsIgnoreCase("HABLAR")) {
			hablar();
		}
		else if(comando.equalsIgnoreCase("ENVIAR")) {
			enviar();
		}
		else if(comando.equalsIgnoreCase("BUSCAR USUARIOS")) {
			buscarUsuarios();
		}
		else if(comando.equalsIgnoreCase("BUSCAR NICKNAME")) {
			buscarNickname();
		}
		else if(comando.equalsIgnoreCase("VER CONTACTOS")) {
			verContactos();
		}
		else if(comando.equalsIgnoreCase("VOLVER")) {
			volverAChat();
		}
		else {
			System.out.println("Se recibio el "+comando);
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if(!e.getValueIsAdjusting()) { 
			Conversacion c = vista.getConversacionSelected();
			if(c != null) {
				//MUESTRO CHAT DE CONVERSACION
			}
		}
	}

}
