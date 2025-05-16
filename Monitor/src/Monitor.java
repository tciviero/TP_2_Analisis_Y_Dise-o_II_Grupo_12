package src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Monitor {
	private static Monitor instance = null;
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
        // Lanzar hilo de escucha de clientes
        new Thread(this::escucharClientes).start();
    }

    private void monitorearPrimario() {
        while (true) {
            try (Socket socket = new Socket(ip_primario, 9999)) { //puerto para ping/echo
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                out.writeUTF("PING");
                out.flush();

                DataInputStream in = new DataInputStream(socket.getInputStream());
                String respuesta = in.readUTF();
                if (respuesta.equalsIgnoreCase("ECHO")) {
                    System.out.println("Primario activo.");
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

    private void escucharClientes() {
        try{
        	InetAddress direccion = InetAddress.getByName(IP_Monitor);
            ServerSocket serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(direccion, PUERTO_MONITOR));
        	System.out.println("Monitor escuchando solicitudes de clientes en " + IP_Monitor + " puerto: " + PUERTO_MONITOR);

            while (true) {
                Socket cliente = serverSocket.accept();
                new Thread(() -> manejarCliente(cliente)).start();
            }
        } catch (IOException e) {
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
                        //sincronizar
                        //le tengo que avisar al primario que sincronice con el secundario
                        //despues lo que pasa por el primario lo tiene que pasar al secundario
                        
                    } else {
                        out.writeUTF("ya_hay_dos_servidores");
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error manejando cliente en el monitor: " + e.getMessage());
        }
    }
	
}
