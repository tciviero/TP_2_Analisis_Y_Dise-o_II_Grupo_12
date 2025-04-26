package modelo.usuario;

public class UsuarioYEstado {
	private String nickname;
	private boolean estado;
	
	public UsuarioYEstado(String nickname, String estado) {
		this.nickname=nickname;
		this.estado = estado.equalsIgnoreCase("Online");	
	}

	public String getNickname() {
		return nickname;
	}

	public boolean isEstado() {
		return estado;
	}

	public String getEstado() {
		if(estado) {
			return "Online";
		}
		else {
			return "Offline";
		}
	}
	

}
