package modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Conversacion implements IFuncionalidadConversacion{
	private ArrayList<Mensaje> mensajes;
	private int cantidadMensajesSinLeer;
	private String nickname;//De usuario o contacto 
	private LocalDateTime  fecha_creacion;
	//Podes tener una conversacion con alguien que no agendaste
	
	public Conversacion(String nickname){
		this.nickname=nickname;
		this.mensajes = new ArrayList<Mensaje>();
		this.fecha_creacion=LocalDateTime.now();
	}
	
	public int getCantidadMensajesSinLeer() {
		return cantidadMensajesSinLeer;
	}
	
	public void SetCantidadMensajesSinLeer(int num) {
		cantidadMensajesSinLeer = num;
	}
	
	public String toString() {
		return (cantidadMensajesSinLeer > 0 ? "(" + cantidadMensajesSinLeer + ") " : "") + nickname + " " + (mensajes.size() > 0 ? mensajes.getLast().toStringSE() : "");
	}
	
	public String mostrarMensajes() {
		StringBuilder sb = new StringBuilder();
		sb.append("Conversación creada " +fecha_creacion.format(DateTimeFormatter.ofPattern("yyyy-MM-dd  hh-mm a"))).append("\n").append("\n");
        for (Mensaje mensaje : mensajes) {
            sb.append(mensaje.toString()).append("\n");
        }
        return sb.toString();
	}

	public void addMensaje(String nombreMensaje, String texto, boolean elMensajeEsPropio) {
		mensajes.add(new Mensaje(nombreMensaje,texto, elMensajeEsPropio));
		cantidadMensajesSinLeer++;
	}
	
	public ArrayList<Mensaje> getMensajes() {
		return mensajes;
	}

	public Object getNickName() {
		return nickname;
	}

}
