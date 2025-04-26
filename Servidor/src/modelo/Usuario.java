package modelo;

import java.util.ArrayList;

public class Usuario {
	
	private String nickname;
	private boolean Conectado;
	private ArrayList<Mensaje> mensajesPendientes;
	
	
	public Usuario(String nickname) {
		this.nickname = nickname;
		this.mensajesPendientes= new ArrayList<Mensaje>();
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


	public ArrayList<Mensaje> getMensajesPendientes() {
		return mensajesPendientes;
	}

}
