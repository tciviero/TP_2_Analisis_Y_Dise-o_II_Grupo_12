package modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Servidor {
	private String ip;
	private int puerto;
	private String ROL;
	private boolean isConectado;
	private LocalDateTime ultimoPing;
	private DateTimeFormatter formato = DateTimeFormatter.ofPattern("[dd/MM-HH:mm:ss]");

    
	public Servidor(String rol,String ip, int puerto) {
		this.ultimoPing=LocalDateTime.now();
        this.ip = ip;
        this.puerto = puerto;
        this.ROL = rol;
        this.formato=DateTimeFormatter.ofPattern("[dd/MM-HH:mm:ss]");
    }

	public String getIp() {
		return ip;
	}

	public int getPuerto() {
		return puerto;
	}
	
	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setPuerto(int puerto) {
		this.puerto = puerto;
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
		return "Servidor ["+ROL+"]  [ip=" + ip + ":" + puerto + "] ["+estado+"]["+ultimoPing.format(formato)+"]";
	}

	public void setConected(boolean b) {
		this.isConectado=b;
	}

	public void setLastPing(LocalDateTime ping,boolean isConectado) {
		this.isConectado=isConectado;
		this.ultimoPing=ping;
	}

	public void setLastPing(boolean b) {
		this.isConectado=b;
		this.ultimoPing=LocalDateTime.now();

	}
	
	
	
	
}
