package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Properties;

public class MainMonitor {
	private static String ip="";
	private static int puerto,cantidadDeServidores;
    private static ServerSocket monitorSocket;
	

	public static void main(String[] args) {
		CargaArchivos();
		System.out.println("Se procede a cargar trabajar en ip:puerto="+ip+":"+puerto+" con ["+cantidadDeServidores+"] servidores");
		Iniciar();
	}
	
	public static void Iniciar() {
        new Thread(() -> {
            try {
            	monitorSocket = new ServerSocket(puerto);
                System.out.println("Monitor iniciado esperando en puerto " + puerto);

                while (true) {
                    Socket socket = monitorSocket.accept();
                 	System.out.println("Servidor conectado desde " + socket.getInetAddress());

                    // crea el hilo para las solicitudes
                    new Thread(() -> manejarServidor(socket)).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

	
	private static void manejarServidor(Socket socket) {
		//Aca llega un servidor nuevo cuando se comunica con el monitor
		//Hay que ver que servidor es, n=1,n=2,... hasta Cantidad de Servidores
		//Asignarle un Ip y Puerto.
		//Asignarle ROL, Primario o secundario
		
	}

	private static void CargaArchivos() {
        String tempDir = System.getProperty("java.io.tmpdir");
        String currentDir = System.getProperty("user.dir");

        File fileTemp = new File(tempDir, "monitor.properties");
        File fileCurrent = new File(currentDir, "monitor.properties");
        
        ip=crearIP();
        puerto=125;
        cantidadDeServidores=3;
        if(!fileCurrent.exists()) {
        	//Si no existe el current se crean los archivos por defecto
        	//El usuario puede modificar esos archivos y volver a ejecutar
        	CrearDefault(fileCurrent);
        	CrearDefault(fileTemp);
        }
        else {
        	//Si existe el current se debe leer de ahí y actualizar el temporal.
        	LeerActual(fileCurrent);
        	ActualizaTemp(fileTemp);
        }
	}

	private static void ActualizaTemp(File fileTemp) {
		Properties properties = new Properties();
    	properties.setProperty("monitor.ip", ip);
    	properties.setProperty("monitor.port", Integer.toString(puerto));	//Puerto por defecto es el 125
    	properties.setProperty("monitor.servidores", Integer.toString(cantidadDeServidores));	//Cantidad de servidores por defecto es 3
    	
    	
    	try (FileOutputStream fos = new FileOutputStream(fileTemp)) {
    		properties.store(fos, "Monitor Configuration");
    		System.out.println("Archivo creado: " + fileTemp.getAbsolutePath());
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
	}

	private static void LeerActual(File fileCurrent) {
		Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream(fileCurrent)) {
            properties.load(fis);
            
            ip = properties.getProperty("monitor.ip");
            puerto= Integer.parseInt(properties.getProperty("monitor.port"));
            cantidadDeServidores= Integer.parseInt(properties.getProperty("monitor.servidores"));
            
            System.out.println("Se leyo actual: " + ip + ":" + puerto+" cantServ="+cantidadDeServidores);
        } catch (IOException e) {
            e.printStackTrace();
        }
	}



	private static void CrearDefault(File Default) {
		Properties properties = new Properties();
    	properties.setProperty("monitor.ip", crearIP());
    	properties.setProperty("monitor.port", "125");	//Puerto por defecto es el 125
    	properties.setProperty("monitor.servidores", "3");	//Cantidad de servidores por defecto es 3
    	
    	try (FileOutputStream fos = new FileOutputStream(Default)) {
    		properties.store(fos, "Monitor Configuration");
    		System.out.println("Archivo creado: " + Default.getAbsolutePath());
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
	}

	public static String crearIP() {
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
						//System.out.println("IP privada real: " + addr.getHostAddress());
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
