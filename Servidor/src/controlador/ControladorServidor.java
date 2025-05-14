package controlador;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import exception.PuertoYaUsadoException;
import modelo.Directorio;
import modelo.Servidor;
import vista.VentanaServidor;


public class ControladorServidor{

	private static ControladorServidor instance=null;
	private VentanaServidor vista;
	private String IP_Servidor = "192.168.1.45";
	private static final int PUERTO_SERVIDOR_PRIMARIO = 1234;
	private static final int PUERTO_SERVIDOR_SECUNDARIO = 1235;
	private Servidor servidor1,servidor2;
	
	private ControladorServidor() {
		this.servidor1 = new Servidor(PUERTO_SERVIDOR_PRIMARIO,PUERTO_SERVIDOR_SECUNDARIO);
		this.servidor2 = new Servidor(PUERTO_SERVIDOR_SECUNDARIO,PUERTO_SERVIDOR_PRIMARIO);
	}
	
	public static ControladorServidor getInstance() {
		if(instance==null) {
			instance = new ControladorServidor();
		}
		return instance;
	}
	
	public VentanaServidor getVista() {
		return vista;
	}
	
	public void Iniciar() {
		IP_Servidor = crearIP();
		try {
			this.servidor1.Iniciar();
			vista = new VentanaServidor(IP_Servidor,PUERTO_SERVIDOR_PRIMARIO);
		} catch (PuertoYaUsadoException e) {
			try {
				this.servidor2.Iniciar();
				vista = new VentanaServidor(IP_Servidor,PUERTO_SERVIDOR_SECUNDARIO);
			} catch (PuertoYaUsadoException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	public void ActualizarVistas() {
		String[] usuarios=null;
//		usuarios = Directorio.getInstance().getUsuarios();
		this.vista.ActualizarDirectorio(Directorio.getInstance().getUsuarios());
	}
	
	public String crearIP() {
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
						System.out.println("IP privada real: " + addr.getHostAddress());
					}
				}
			}
			return addr.getHostAddress();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	

}
