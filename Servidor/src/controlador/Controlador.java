package controlador;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import modelo.Servidor;
import vista.VentanaServidor;


public class Controlador{

	private VentanaServidor vista;
	private String IP_Servidor = null;
	private int Puerto_Servidor;
	
	public Controlador() {
	}
	
	public VentanaServidor getVista() {
		return vista;
	}
	
	public void Iniciar() {
		IP_Servidor = crearIP();
		Puerto_Servidor = 1234;
		vista = new VentanaServidor(IP_Servidor,Puerto_Servidor);
		Servidor.getInstancia().Iniciar(IP_Servidor,Puerto_Servidor);
		
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
