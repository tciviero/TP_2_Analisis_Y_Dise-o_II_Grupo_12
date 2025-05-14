package controlador;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Properties;

import modelo.Directorio;
import modelo.Servidor;
import vista.VentanaServidor;


public class ControladorServidor{

	private static ControladorServidor instance=null;
	private VentanaServidor vista;
	private String IP_Servidor = "192.168.1.45";
	private int Puerto_Servidor = 1234;
	private String ip_monitor;
	private int puerto_monitor;
	
	
	private ControladorServidor() {
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
		/*Bueno Es lo primero que se ejecuta
			Primero se debe ejecutar el programa monitor, que es el que crea un archivo de configuracion.
			En ese archivo debe estar la ip del monitor.
		*/
		leeArchTemp();	//Lee archivo temporal monitor.properties
		//Y carga los valores ip_monitor y puerto_monitor
		
		FALTA comunicarse con el monitor para que nos de ip puerto y rol=primario o secundario.
		
		FALTA tambien toda la parte de sincronizacion, el primario le deberiar pasar
		a los servidores secundarios todas las solicitudes que recibe.
		
		IP_Servidor = crearIP();
		Puerto_Servidor = 1234;
		vista = new VentanaServidor(IP_Servidor,Puerto_Servidor);
		Servidor.getInstancia().Iniciar();
	}
	
	private void leeArchTemp() {
        String tempDir = System.getProperty("java.io.tmpdir");
        File fileTemp = new File(tempDir, "monitor.properties");
        while(!fileTemp.exists()) {
        	System.out.println("Esperando a que el monitor cree el archivo...");
            try {
                Thread.sleep(5000); // Espera de 5 segundos
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream(fileTemp)) {
            properties.load(fis);
            ip_monitor = properties.getProperty("monitor.ip");
            puerto_monitor = Integer.parseInt(properties.getProperty("monitor.port"));
            System.out.println("Conectando al monitor en " + ip_monitor + ":" + puerto_monitor);
        } catch (IOException e) {
            e.printStackTrace();
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
