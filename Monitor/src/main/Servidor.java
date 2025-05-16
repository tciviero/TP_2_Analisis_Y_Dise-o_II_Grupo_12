package main;

import java.io.IOException;
import java.net.Socket;

public class Servidor {
	private Socket socket;
	private String ip;
	private int puerto,numero;
	private boolean isConectado;
	
	private ProcessBuilder processBuilder;
    private Process proceso;

    
	public Servidor(int numero,String ip, int puerto) {
        this.ip = ip;
        this.puerto = puerto;
        this.numero = numero;
        this.isConectado=false;
        
        String currentDir = System.getProperty("user.dir");
        String servidorJar = currentDir + "/servidor.jar";
        String comando = "java -jar " + servidorJar + " " + ip + " " + puerto;
        this.processBuilder = new ProcessBuilder(comando.split(" "));
        this.processBuilder.inheritIO(); // Redirige la salida estándar y de error
    }
	
	public void iniciar() {
		this.isConectado=false;
        try {
            this.proceso = processBuilder.start();
            this.isConectado=true;
            System.out.println("Servidor iniciado en " + ip + ":" + puerto + " con PID: " + proceso.pid());
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error al iniciar el servidor en " + ip + ":" + puerto);
        }
    }
	
	public void detener() {
        if (proceso != null) {
            proceso.destroy();
            System.out.println("Servidor detenido en " + ip + ":" + puerto);
        }
        this.isConectado=false;
    }

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public Socket getSocket() {
		return socket;
	}

	public String getIp() {
		return ip;
	}

	public int getPuerto() {
		return puerto;
	}

	@Override
	public String toString() {
		String estado ="";
		if(isConectado) {
			estado="Online";
		}
		else {
			estado="OffLine";
		}
		return "Servidor ["+numero+"]  [ip=" + ip + ":" + puerto + "] ["+estado+"]";
	}

	public void setConected(boolean b) {
		this.isConectado=b;
	}
	
	
	
	
	
}
