package controlador;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;

import exception.PuertoYaUsadoException;
import modelo.Directorio;
import modelo.Servidor;
import vista.VentanaServidor;


public class ControladorServidor{

	private static ControladorServidor instance=null;
	private VentanaServidor vista;
	private String IP_servidor1 = "192.168.1.101";
	private String IP_servidor2 = "192.168.1.102";
	private static final int PUERTO_SERVIDOR_PRIMARIO = 777;
	private static final int PUERTO_SERVIDOR_SECUNDARIO = 666;
	private Servidor servidor1,servidor2;
	private String IP_Monitor = "192.168.1.100";
	private static final int PUERTO_MONITOR = 8888;
	
	
	private ControladorServidor() {
		this.servidor1 = new Servidor(IP_servidor1,PUERTO_SERVIDOR_PRIMARIO,IP_Monitor,PUERTO_MONITOR);
		this.servidor2 = new Servidor(IP_servidor2,PUERTO_SERVIDOR_SECUNDARIO,IP_Monitor,PUERTO_MONITOR);
	}
	
	public static ControladorServidor getInstance() {
		if(instance==null) {
			instance = new ControladorServidor();
		}
		return instance;
	}
	
	public void Iniciar() {
		try {
			vista = new VentanaServidor(IP_servidor1,PUERTO_SERVIDOR_PRIMARIO);
			this.servidor1.iniciar();
			vista.setROL("Primario");
		} catch (PuertoYaUsadoException e) {
			try {
				vista = new VentanaServidor(IP_servidor2,PUERTO_SERVIDOR_SECUNDARIO);
				vista.setVisible(true);
				this.servidor2.iniciar();
				vista.setROL("Secundario");
			} catch (PuertoYaUsadoException e1) {
				System.out.println("ya estan los 2 abiertos");
				System.out.println("PuertoYaUsadoException INICIAR en CONTROLADOR SERVIDOR");
			}
		}
		vista.setVisible(true);
		
	}
		
	
	public VentanaServidor getVista() {
		return vista;
	}
	
	public void ActualizarVistas(ArrayList<String> usuarios) {
		this.vista.ActualizarDirectorio(usuarios);
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
