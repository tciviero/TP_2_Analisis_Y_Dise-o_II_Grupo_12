package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;

public class MainMonitor {
	private static VentanaMonitor vista;
	
	private static String ipMonitor="";
	private static int puertoMonitor,cantidadDeServidores;
    private static ServerSocket monitorSocket;
    
    private static int CantServidoresActivos;
	private static ArrayList<Servidor> servidores;

	public static void main(String[] args) {
		vista = new VentanaMonitor();
		vista.setVisible(true);
		
		servidores = new ArrayList<Servidor> ();
		CantServidoresActivos=0;
		
		CargaArchivos();
		vista.ActualizarServidores(servidores);
		
		System.out.println("Se procede a cargar trabajar en ip:puerto="+ipMonitor+":"+puertoMonitor+" con ["+cantidadDeServidores+"] servidores");
		IniciarMonitor();
	}
	
	public static void IniciarMonitor() {
        new Thread(() -> {
            try {
            	monitorSocket = new ServerSocket(puertoMonitor);
                System.out.println("Monitor iniciado esperando en puerto " + puertoMonitor);

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
        
        //en el hilo de arriba se queda escuchando a los posibles servidores que se conecten
        //Aca invocamos a la cantidad de servidores que se configuro en el archivo
        for (Servidor s : servidores) {
            s.iniciar();
        }
        
        
    }

	
	private static void manejarServidor(Socket socket) {
		//Aca llega un servidor nuevo cuando se comunica con el monitor
		//Hay que ver que servidor es, n=1,n=2,... hasta Cantidad de Servidores
		//Asignarle un Ip y Puerto.
		//Asignarle ROL, Primario o secundario
		
		
		if(CantServidoresActivos<cantidadDeServidores) {
			//Necesito saber que ip y que puerto asignarle.
			
			//servidores.add(new Servidor(socket,));
		}
		else{
			//La cantidad de instancias de servidor ya esta cubierta.
			//Se le deberia indicar al servidor que se comunica en el socket que se cierre
			
		}
	}

	private static void CargaArchivos() {
        String tempDir = System.getProperty("java.io.tmpdir");
        String currentDir = System.getProperty("user.dir");

        File fileTemp = new File(tempDir, "monitor.properties");
        File fileCurrent = new File(currentDir, "monitor.properties");
        
        ipMonitor=crearIP();
        puertoMonitor=125;
        cantidadDeServidores=3;
        if(!fileCurrent.exists()) {
        	//Si no existe el current se crean los archivos por defecto
        	//El usuario puede modificar esos archivos y volver a ejecutar
        	CrearDefault(fileCurrent);
        	CrearDefault(fileTemp);
        	String ipcomun=crearIP();
        	servidores.add(new Servidor(1,ipcomun,1001));
        	servidores.add(new Servidor(2,ipcomun,1002));
        	servidores.add(new Servidor(3,ipcomun,1003));
        	
        }
        else {
        	//Si existe el current se debe leer de ahí y actualizar el temporal.
        	LeerActual(fileCurrent);
        	ActualizaTemp(fileTemp);
        }
	}

	private static void ActualizaTemp(File fileTemp) {
		Properties properties = new Properties();
    	properties.setProperty("monitor.ip", ipMonitor);
    	properties.setProperty("monitor.port", Integer.toString(puertoMonitor));	//Puerto por defecto es el 125
    	properties.setProperty("monitor.servidores", Integer.toString(cantidadDeServidores));	//Cantidad de servidores por defecto es 3
    	
    	for (int i = 0; i < cantidadDeServidores; i++) {
            String ipKey = "servidor" + (i+1) + ".ip";
            String puertoKey = "servidor" + (i+1) + ".puerto";
            properties.setProperty(ipKey, servidores.get(i).getIp());
            properties.setProperty(puertoKey, Integer.toString(servidores.get(i).getPuerto()));
        }
    	
    	try (FileOutputStream fos = new FileOutputStream(fileTemp)) {
    		properties.store(fos, "Monitor Configuration");
    		System.out.println("Se crea archivo en carpeta temp: " + fileTemp.getAbsolutePath());
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
	}

	private static void LeerActual(File fileCurrent) {
		Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream(fileCurrent)) {
            properties.load(fis);
            
            ipMonitor = properties.getProperty("monitor.ip");
            puertoMonitor= Integer.parseInt(properties.getProperty("monitor.port"));
            cantidadDeServidores= Integer.parseInt(properties.getProperty("monitor.servidores"));
            servidores.clear();
            for (int i = 1; i <= cantidadDeServidores; i++) {
                String ipKey = "servidor" + i + ".ip";
                String puertoKey = "servidor" + i + ".puerto";
                String servidorIp = properties.getProperty(ipKey);
                int servidorPuerto = Integer.parseInt(properties.getProperty(puertoKey));
                servidores.add(new Servidor(i,servidorIp, servidorPuerto));
            }
            
            
            System.out.println("Se leyo actual: " + ipMonitor + ":" + puertoMonitor+" cantServ="+cantidadDeServidores);
            for (Servidor servidor : servidores) {
                System.out.println("  " + servidor.getIp() + ":" + servidor.getPuerto());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
	}



	private static void CrearDefault(File Default) {
		Properties properties = new Properties();
		String ipcomun=crearIP();
    	properties.setProperty("monitor.ip", ipcomun);
    	properties.setProperty("monitor.port", "125");	//Puerto por defecto es el 125
    	properties.setProperty("monitor.servidores", "3");	//Cantidad de servidores por defecto es 3
    	properties.setProperty("servidor1.ip", ipcomun);
    	properties.setProperty("servidor1.puerto", "1001");
    	properties.setProperty("servidor2.ip", ipcomun);
    	properties.setProperty("servidor2.puerto", "1002");
    	properties.setProperty("servidor3.ip", ipcomun);
    	properties.setProperty("servidor3.puerto", "1003");
    	
    	
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
