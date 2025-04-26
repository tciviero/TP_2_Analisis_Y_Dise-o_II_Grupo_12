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

import modelo.Conversacion;
import modelo.IVerConversacion;
import modelo.Contacto.Contacto;
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
			if(!Usuario.getInstancia().isConectado()) {
				Usuario.getInstancia().Iniciar(nombre, IP_Usuario, puerto);
				Usuario.getInstancia().Conectar();
			}
			Usuario.getInstancia().enviarRequestRegistro();
		} catch (SocketTimeoutException e) {
			System.out.println("No fue posible conectarse con el servidor");
		}catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void inicioSesion() {
		String nombre = this.vista.getNickNameUsuarioText();
		int puerto = Integer.parseInt(vista.getPuertoUsuarioText());
		
		try {
			if(!Usuario.getInstancia().isConectado()) {
				Usuario.getInstancia().Iniciar(nombre, IP_Usuario, puerto);
				Usuario.getInstancia().Conectar();
				System.out.println("Usuario conectado");
			}
			else {
				System.out.println("Se rompe cuando se llama a enviarRequest??");
				Usuario.getInstancia().enviarRequestInicioSesion();
			}
		} catch (SocketTimeoutException e) {
			System.out.println("No fue posible conectarse con el servidor");
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	private void agendar() {
		UsuarioYEstado usuario = vista.getUsuarioSeleccionado();
		System.out.println("Se desea agendar a"+usuario.getNickname());
		Usuario.getInstancia().agendarContacto(usuario.getNickname());
		vista.ActualizaListaContactos();
	}
	
	private void hablar() {
		/*
		IVerConversacion contactoSeleccionado = vista.getContactoSeleccionado();
		contactoSeleccionado.SetCantidadMensajesSinLeer(0);
		vista.CargarChat(contactoSeleccionado.mostrarMensajes());
		vista.ContactoSeleccionadoEsChat();*/
		
		//Si la conversacion no existe se crea
		//
		Contacto seleccionado = vista.getContactoSeleccionado();
		Conversacion aAbrir = Usuario.getInstancia().getConversacion(seleccionado.getNickName());
		vista.CargarChat(aAbrir);
		vista.ActualizarListaConversaciones();
		
		
		System.out.println("Dentro de hablar()");
	}
	
	private void enviar() {/*
		String msg = vista.getTecladoText();
		if(!msg.equalsIgnoreCase("")) {
			try {
				this.usuario.Envia(vista.getConversacionAbierta(), msg);
			} catch (IOException e) {
				vista.OnFalloEnvioMensaje();
			}
			vista.getConversacionAbierta().SetCantidadMensajesSinLeer(0);
			vista.setTecladoText("");
			vista.CargarChat(vista.getConversacionAbierta().mostrarMensajes());
			vista.ActualizaListaContactos();
		}*/
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String comando = e.getActionCommand();
		System.out.println("Se apreto "+comando);
		if(comando.equalsIgnoreCase("REGISTRAR")) {
			registrar();
		}
		else if(comando.equalsIgnoreCase("INICIAR")) {
			inicioSesion();
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
			Conversacion c = vista.getConversacionSelected();
			if(c != null) {
				//MUESTRO CHAT DE CONVERSACION
			}
		}
	}

}
