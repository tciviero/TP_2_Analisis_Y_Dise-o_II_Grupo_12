package src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.BindException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Enumeration;

public class Monitor {
	private static Monitor instance = null;
	private static VentanaMonitor vista;
	
	private String IP_Monitor = "192.168.1.100";
	private static final int PUERTO_MONITOR = 8888;
	
    private String ip_primario,ip_secundario;
    private int puertoPrimario, puertoSecundario;
	
	private Monitor() {
		this.puertoPrimario = 0;
		this.puertoSecundario = 0;
	}
	
	public static Monitor get_instance() {
		if (instance == null)
			instance = new Monitor();
		return instance;
	}
	
	public void iniciar() {
		vista = new VentanaMonitor();
		vista.setVisible(true);
        // Lanzar hilo de escucha de clientes
        new Thread(this::escucharClientes).start();
    }
	
    private void escucharClientes() {
        try{
        	InetAddress direccion = InetAddress.getByName(IP_Monitor);
        	ServerSocket serverSocket = new ServerSocket();
            try {
            	serverSocket.bind(new InetSocketAddress(direccion, PUERTO_MONITOR));
            }catch (BindException e) {	//esta excepción sale cuando el ip no coincide con la de la pc
            	String IpActual=crearIP();
            	System.out.println("La ip ["+IP_Monitor+"] genera una BindException se carga el monitor en ["+IpActual+"]");
            	direccion = InetAddress.getByName(IpActual);
            	serverSocket.bind(new InetSocketAddress(direccion, PUERTO_MONITOR));
            	this.IP_Monitor = IpActual;
            }
        	
        	System.out.println("Monitor escuchando solicitudes de clientes en " + IP_Monitor + " puerto: " + PUERTO_MONITOR);

            while (true) {
                Socket cliente = serverSocket.accept();
                new Thread(() -> manejarCliente(cliente)).start();
            }
            
            
        } catch (BindException e2){ //Esta excepción puede salir porque el ip.puerto ya está en uso
        	if(e2.getMessage().contains("Address already in use")) {
        		System.out.println("El ip-puerto donde se desea iniciar el monitor ya está en uso");
        	}
        }catch (IOException e) {
            e.printStackTrace();
        } 
    }
    
    private void manejarCliente(Socket socket) {
        try (DataInputStream in = new DataInputStream(socket.getInputStream());
             DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {

            String data = in.readUTF();
			System.out.println("El monitor recibio este mensaje: "+data);
			String[] dataArray = data.split("`");
			
			String comando = dataArray[0].toUpperCase();
            if (comando.equalsIgnoreCase("CUAL_PRIMARIO")) {
               if(this.puertoPrimario!=0) {
            	   out.writeUTF(ip_primario + "`" + puertoPrimario);
               }else {
            	   out.writeUTF("PRIMARIO`NO_HAY");
               }
            }else if (comando.equalsIgnoreCase("servidor_conectado")) {
                synchronized (this) {
                    if (puertoPrimario == 0) {
                        this.ip_primario = dataArray[1];
                    	this.puertoPrimario = Integer.parseInt(dataArray[2]);
                        System.out.println("Servidor designado como PRIMARIO " + ip_primario + " puerto: " + puertoPrimario);
                       
                        out.writeUTF("sos_primario");
                        // hilo de monitoreo
                        try {
                            Thread.sleep(2000); // Esperar 5 segundos antes del próximo ping
                            new Thread(this::monitorearPrimario).start();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else if (puertoSecundario == 0) {
                    	this.ip_secundario = dataArray[1];
                    	this.puertoSecundario = Integer.parseInt(dataArray[2]);
                        System.out.println("Servidor designado como SECUNDARIO " + ip_secundario + " puerto: " + puertoSecundario);
                        out.writeUTF("sos_secundario");
                        //mando a sincronizar al principal con el secundario recien conectado
                        Socket socket_prin = new Socket(ip_primario, puertoPrimario);
                        DataOutputStream out_prin = new DataOutputStream(socket_prin.getOutputStream());
                        out_prin.writeUTF("SINCRONIZAR`"+ip_secundario+"`"+puertoSecundario);
                        out_prin.flush();
                        //sincronizar
                        //le tengo que avisar al primario que sincronice con el secundario
                        //despues lo que pasa por el primario lo tiene que pasar al secundario
                        try {
                            Thread.sleep(2000); // Esperar 5 segundos antes del próximo ping
                            new Thread(this::monitorearSecundario).start();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        
                    } else {
                        out.writeUTF("ya_hay_dos_servidores");
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error manejando cliente en el monitor: " + e.getMessage());
        }
    }

    private void monitorearSecundario() {
    	while (true) {
            try (Socket socket = new Socket(ip_secundario, puertoSecundario)) { //puerto para ping/echo
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                out.writeUTF("PING");
                out.flush();

                DataInputStream in = new DataInputStream(socket.getInputStream());
                String respuesta = in.readUTF();
                if (respuesta.equalsIgnoreCase("ECHO")) {
                	//System.out.println("Secundario activo.");
                }
            } catch (IOException e) {
                //Se caé el secundario
                this.puertoSecundario = 0;
                DateTimeFormatter formato = DateTimeFormatter.ofPattern("[dd/MM-HH:mm:ss]");
                System.out.println("Ping Fallido servidor secundario ["+LocalDateTime.now().format(formato)+"]");
            }
            
            try {
                Thread.sleep(5000); // Esperar 5 segundos antes del próximo ping
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void monitorearPrimario() {
        while (true) {
            try (Socket socket = new Socket(ip_primario, puertoPrimario)) { //puerto para ping/echo
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                out.writeUTF("PING");
                out.flush();

                DataInputStream in = new DataInputStream(socket.getInputStream());
                String respuesta = in.readUTF();
                if (respuesta.equalsIgnoreCase("ECHO")) {
                    //Hay que actualizar en la interfaz el ultimo echo del servidor primario
                	//System.out.println("Primario activo.");
                }
            } catch (IOException e) {
                //e.printStackTrace();
            	System.out.println("Primario no responde. Promoviendo secundario...");
                this.puertoPrimario = 0;
            	if(this.puertoSecundario!=0) {
                	promoverSecundario();
                }else {
                	System.out.println("no hay ningun secundario para promover");
                	break;
                }
            }

            try {
                Thread.sleep(5000); // Esperar 5 segundos antes del próximo ping
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void promoverSecundario() {
        try (Socket socket = new Socket(ip_secundario, puertoSecundario)) {
        	this.ip_primario = ip_secundario;
        	this.puertoPrimario = puertoSecundario;
        	this.puertoSecundario = 0;
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            out.writeUTF("SOS_PRIMARIO");
            out.flush();
        } catch (IOException e) {
            System.err.println("Error al promover al secundario: " + e.getMessage());
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
