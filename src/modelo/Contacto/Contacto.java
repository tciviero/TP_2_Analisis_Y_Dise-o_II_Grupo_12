package modelo.Contacto;

import java.util.ArrayList;

import modelo.Mensaje;


public class Contacto implements IFuncionalidadContacto {
	private int puerto;
	private String nickName, ip;
	private ArrayList<Mensaje> mensajes;
	private int cantidadMensajesSinLeer;
	
	public Contacto(String nombre, String ip, int puerto){
		this.puerto = puerto;
		this.ip = ip;
		this.nickName = nombre;
		this.mensajes = new ArrayList<Mensaje>();
	}
	
	public int getCantidadMensajesSinLeer() {
		return cantidadMensajesSinLeer;
	}
	
	@Override
	public void SetCantidadMensajesSinLeer(int num) {
		cantidadMensajesSinLeer = num;
	}

	public String getNickName() {
		return nickName;
	}
	
	public void setNickName(String nuevo) {
		this.nickName = nuevo;
	}

	public int getPuerto() {
		return puerto;
	}

	public String getIp() {
		return ip;
	}
	
	@Override
	public String toString() {
		return (cantidadMensajesSinLeer > 0 ? "(" + cantidadMensajesSinLeer + ") " : "") + nickName + " " + (mensajes.size() > 0 ? mensajes.getLast().toStringSE() : "");
	}
	
	@Override
	public String mostrarMensajes() {
		StringBuilder sb = new StringBuilder();
		//sb.append("Conversación creada " +fecha_creacion.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))).append("\n").append("\n");
        for (Mensaje mensaje : mensajes) {
            sb.append(mensaje.toString()).append("\n");
        }
        return sb.toString();
	}

	@Override
	public void addMensaje(String nombreMensaje, String texto, boolean elMensajeEsPropio) {
		mensajes.add(new Mensaje(nombreMensaje,texto, elMensajeEsPropio));
		cantidadMensajesSinLeer++;
	}

}
