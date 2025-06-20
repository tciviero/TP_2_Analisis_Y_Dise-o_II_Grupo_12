package src.src;

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
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class Monitor {
	private static Monitor instance = null;
	private static VentanaMonitor vista;
	
	private String IP_Monitor = "192.168.1.100";
	private static final int PUERTO_MONITOR = 8888;
	
    private String ip_primario,ip_secundario;
    private int puertoPrimario, puertoSecundario;
    
    private ArrayList<Servidor> servidores;
    private Servidor primario,secundario;

    private Thread hiloMonitoreoPrimario=null;
    private Thread hiloMonitoreoSecundario=null;
    private boolean EstaConectadoSecundario;
    
    List<Socket> sockets_usuarios = new ArrayList<Socket>();
	
	private Monitor() {
		this.puertoPrimario = 0;
		this.puertoSecundario = 0;
		this.servidores=new ArrayList<Servidor>();
		this.primario=new Servidor("Primario",ip_primario,puertoPrimario);
		this.secundario=new Servidor("Secundario",ip_secundario,puertoSecundario);
		
		this.servidores.add(primario);
		this.servidores.add(secundario);
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
        	
        	InetAddress local = InetAddress.getLocalHost();

            ServerSocket serverSocket = new ServerSocket();
            try {
                serverSocket.bind(new InetSocketAddress(local.getHostAddress(), PUERTO_MONITOR));
            }catch (BindException e) {    //esta excepción sale cuando el ip no coincide con la de la pc
                String IpActual=crearIP();
                System.out.println("La ip ["+IP_Monitor+"] genera una BindException se carga el monitor en ["+IpActual+"]");
                serverSocket.bind(new InetSocketAddress(local.getHostAddress(), PUERTO_MONITOR));
                this.IP_Monitor = IpActual;
            }
        	
        	System.out.println("Monitor escuchando solicitudes de clientes en " + IP_Monitor + " puerto: " + PUERTO_MONITOR);

            while (true) {
                Socket cliente = serverSocket.accept();		//ESCUCHA EL PUERTO COMUN del monitor
                manejarCliente(cliente);//new Thread(() -> manejarCliente(cliente));
                /*if(this.hiloMonitoreoSecundario==null) {
                	this.hiloMonitoreoSecundario=new Thread(() -> manejarCliente(cliente));
                	this.hiloMonitoreoSecundario.start();
                }*/
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
    	//Hay un nuevo cliente o un nuevo servidor conectado en el socket
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
                    	this.primario.setIp(ip_primario);
                    	this.primario.setPuerto(puertoPrimario);
                        System.out.println("Servidor designado como PRIMARIO " + ip_primario + " puerto: " + puertoPrimario);
                       
                        out.writeUTF("sos_primario");
                        System.out.println("Se envia sos_primario");
                        // hilo de monitoreo
                        if(this.hiloMonitoreoPrimario==null) {
                        	this.hiloMonitoreoPrimario=new Thread(this::monitorearPrimario);
                        	this.hiloMonitoreoPrimario.start();
                        }
                        else {
                        	this.hiloMonitoreoPrimario.interrupt();
                        	this.hiloMonitoreoPrimario=new Thread(this::monitorearPrimario);
                        	this.hiloMonitoreoPrimario.start();
                        }
                        
                        
                    } else if (puertoSecundario == 0) {
                    	this.ip_secundario = dataArray[1];
                    	this.puertoSecundario = Integer.parseInt(dataArray[2]);
                    	this.secundario.setIp(ip_secundario);
                    	this.secundario.setPuerto(puertoSecundario);
                    	
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
                        
                        if(this.hiloMonitoreoSecundario==null) {
                        	this.hiloMonitoreoSecundario=new Thread(this::monitorearSecundario);
                        	this.hiloMonitoreoSecundario.start();
                        }
                        else {
                        	if(this.hiloMonitoreoSecundario.isAlive()) {
                        		
                        	}else {
                        		//this.hiloMonitoreoSecundario.interrupt();
                        		//this.hiloMonitoreoSecundario.stop();
                        		//this.hiloMonitoreoSecundario.suspend();
                        		//this.hiloMonitoreoSecundario=new Thread(this::monitorearSecundario);
                        		this.hiloMonitoreoSecundario.start();
                        	}
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
    	System.out.println("Hay un hilo monitoreando secundario");
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("[dd/MM-HH:mm:ss]");
        
    	while (true) {
            try (Socket socket = new Socket(ip_secundario, puertoSecundario)) { //puerto para ping/echo
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                out.writeUTF("PING");
                out.flush();

                DataInputStream in = new DataInputStream(socket.getInputStream());
                String respuesta = in.readUTF();
                if (respuesta.equalsIgnoreCase("ECHO")) {
                	LocalDateTime ping = LocalDateTime.now();
                	this.secundario.setLastPing(true);
                	this.EstaConectadoSecundario=true;
                }
                else {
                	//this.EstaConectadoSecundario=false;
                	//this.puertoSecundario=0;
                }
            } catch (IOException e) {
                //Se caé el secundario
            	this.EstaConectadoSecundario=false;
            	this.puertoSecundario=0;
            	this.secundario.setPuerto(0);
            	this.secundario.setLastPing(false);
                //System.out.println("servidor secundario Caido ["+ping.format(formato)+"]");
            }
            ActualizaServidores();
            try {
                Thread.sleep(5000); // Esperar 5 segundos antes del próximo ping
            } catch (InterruptedException e) {
            	//Thread.currentThread().interrupt();
                	e.printStackTrace();
            }
        }
    }
    
    private void monitorearPrimario() {
    	System.out.println("Hay un hilo monitoreando primario");
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
                	this.primario.setLastPing(true);
                }
            } catch (IOException e) {
                //e.printStackTrace();
            	System.out.println("Primario no responde. Promoviendo secundario...");
                this.puertoPrimario = 0;
                this.primario.setLastPing(false);
            	if(this.puertoSecundario!=0) {
                	promoverSecundario();
                }else {
                	System.out.println("no hay ningun secundario para promover");
                	break;
                }
            }
            ActualizaServidores();
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
        	this.primario.setIp(ip_secundario);
        	this.primario.setPuerto(puertoSecundario);
        	this.secundario.setPuerto(0);
        	this.secundario.setIp(null);
        	ActualizaServidores();
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
	
    public void ActualizaServidores() {
    	Monitor.vista.ActualizarServidores(servidores);
    }
}
