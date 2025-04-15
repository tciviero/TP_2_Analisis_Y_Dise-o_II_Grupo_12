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
import java.util.Enumeration;

import modelo.Contacto.Contacto;
import modelo.Contacto.IVerConversacion;
import modelo.usuario.IFuncionalidadUsuario;
import modelo.usuario.Usuario;
import vista.VentanaChat;

public class Controlador implements ActionListener{
	IFuncionalidadUsuario usuario = Usuario.getInstancia();
	private IVista vista;
	
	private String IP_Usuario = null;
	
	public Controlador() {
		
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
	
	public void Iniciar() {
		IP_Usuario = crearIP();
		vista = new VentanaChat(IP_Usuario);
		vista.addActionListener(this);
		Usuario.getInstancia().AgregarSuscriptor(vista);
	}

	private void conectar() {
		String nombre = this.vista.getNickNameUsuarioText();
		int puerto = Integer.parseInt(vista.getPuertoUsuarioText());
		try {
			DireccionYPuertoEnUso(IP_Usuario,puerto);
			this.usuario.conectar(nombre, IP_Usuario, puerto); //IConectar
			vista.conectado();
		} catch (BindException e) { //puerto ya en uso
			vista.onFalloPuertoYaEnUso();
		} catch (IllegalArgumentException e) { //puerto fuera de rango
			vista.onFalloPuertoFueraRango();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void agregar() {
		String nombreContacto = this.vista.getNombreContactoText();
		String IPContacto = this.vista.getIpContactoText();
		System.out.println("IP: " + IPContacto);
		int puertoContacto = Integer.parseInt(this.vista.getPuertoContactoText());
		System.out.println("Puerto: " + puertoContacto);
		try {
			UsuarioExistente(IPContacto,puertoContacto);
			Contacto nuevoContacto = new Contacto(nombreContacto,IPContacto,puertoContacto);
			this.usuario.agendarContacto(nuevoContacto); //IAgendar
			vista.ActualizaListaContactos();
			vista.OnRegistroContactoExitoso();
		} catch (IllegalArgumentException e) { //puerto fuera de rango
			vista.onFalloPuertoFueraRango();
		} catch (IOException e) {
			vista.onFalloPuertoSinUso();
		}
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
		if(comando.equalsIgnoreCase("CONECTAR")) {
			conectar();
		}
		else if(comando.equalsIgnoreCase("AGREGAR")) {
			agregar();
		}
		else if(comando.equalsIgnoreCase("HABLAR")) {
			hablar();
		}
		else if(comando.equalsIgnoreCase("ENVIAR")) {
			enviar();
		}
		else {
			
		}
	}

}
