package src.src;


public class Servidor {
	private String ip;
	private int puerto,numero;
	private boolean isConectado;

    
	public Servidor(int numero,String ip, int puerto) {
        this.ip = ip;
        this.puerto = puerto;
        this.numero = numero;
        this.isConectado=false;
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
