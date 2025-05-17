package modelo;


public class Usuario {
	
	private String nickname;
	private boolean Conectado;
	
	
	public Usuario(String nickname) {
		this.nickname = nickname;
		this.Conectado=true;
	}


	public boolean isConectado() {
		return Conectado;
	}


	public void setConectado(boolean conectado) {
		Conectado = conectado;
	}


	public String getNickname() {
		return nickname;
	}

	public String getEstado() {
		if(Conectado) {
			return "Online";
		}
		else {
			return "Offline";
		}
	}

}
