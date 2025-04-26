package controlador;

import java.awt.event.ActionEvent;
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

import excepciones.UsuarioConSesionActivaException;
import excepciones.UsuarioNoRegistradoException;
import modelo.Contacto.Contacto;
import modelo.Contacto.IVerConversacion;
import modelo.usuario.IFuncionalidadUsuario;
import modelo.usuario.Usuario;
import modelo.usuario.UsuarioYEstado;
import vista.VentanaChat;

public class Controlador implements ActionListener, ListSelectionListener{
	IFuncionalidadUsuario usuario = Usuario.getInstancia();
	private IVista vista;
	
	private String IP_Usuario = null;
	
	public Controlador() {
		
	}

	public void Iniciar() {
		IP_Usuario = crearIP();
		vista = new VentanaChat(IP_Usuario);
		vista.addActionListener(this);
		vista.addListSelectionListener(this);
		Usuario.getInstancia().AgregarSuscriptor(vista);
	}
	
	
	public IVista getVista(){
		return this.vista;
	}
	
	private void DireccionYPuertoEnUso(String ip,int puerto) throws BindException,IllegalArgumentException, IOException {
        ServerSocket socket = new ServerSocket(puerto);
		socket.close();
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
		int puerto = Integer.parseInt(vista.getPuertoUsuarioText());
		
		try {
			//if(!Usuario.getInstancia().isConectado()) {
				Usuario.getInstancia().Iniciar(nombre, IP_Usuario, puerto);
				Usuario.getInstancia().Conectar();
			//}
			Usuario.getInstancia().enviarRequestRegistro();
		} catch (SocketTimeoutException e) {
			System.out.println("No fue posible conectarse con el servidor");
		}catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void comprobarInicioSesion() {
		String nombre = this.vista.getNickNameUsuarioText();
		int puerto = Integer.parseInt(vista.getPuertoUsuarioText());
		
		try {
			Usuario.getInstancia().Iniciar(nombre, IP_Usuario, puerto);
			Usuario.getInstancia().enviarRequestInicioSesion(nombre); //aca ya comprobo todo
			//ahora toca iniciar sesion
			Usuario.getInstancia().iniciarSesion(nombre);
		} catch (UsuarioConSesionActivaException e) {
			this.vista.onFalloUsuarioConSesionActiva(nombre);
		} catch (UsuarioNoRegistradoException e) {
			this.vista.onFalloUsuarioNoRegistrado(nombre);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*private void inicioSesion() {
		String nombre = this.vista.getNickNameUsuarioText();
		int puerto = Integer.parseInt(vista.getPuertoUsuarioText());
		
		Usuario.getInstancia().Iniciar(nombre, IP_Usuario, puerto);
		Usuario.getInstancia().Conectar();
		
		this.Conectar(); //levanta el socket
		mensajeRegistro = "INICIAR`" + nickname;
		out.writeUTF(mensajeRegistro); //pide iniciar sesion
	}*/
	
	private void agendar() {
		UsuarioYEstado usuario = vista.getUsuarioSeleccionado();
		Usuario.getInstancia().agendarContacto(usuario.getNickname());
		vista.ActualizaListaContactos();
	}
	
	private void hablar() {
		IVerConversacion contactoSeleccionado = vista.getContactoSeleccionado();
		contactoSeleccionado.SetCantidadMensajesSinLeer(0);
		vista.CargarChat(contactoSeleccionado.mostrarMensajes());
		vista.ContactoSeleccionadoEsChat();
	}
	
	private void enviar() {
		String msg = vista.getTecladoText();
		if(!msg.equalsIgnoreCase("")) {
			try {
				this.usuario.Envia(vista.getContactoChat(), msg);
			} catch (IOException e) {
				vista.OnFalloEnvioMensaje();
			}
			vista.getContactoChat().SetCantidadMensajesSinLeer(0);
			vista.setTecladoText("");
			vista.CargarChat(vista.getContactoChat().mostrarMensajes());
			vista.ActualizaListaContactos();
		}
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String comando = e.getActionCommand();
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
		else {
			System.out.println("Se recibio el "+comando);
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if(!e.getValueIsAdjusting()) { 
			Contacto contacto = vista.getConversacionSelected();
			if(contacto != null) {
				//MUESTRO CHAT DE CONVERSACION
			}
		}
	}

}
