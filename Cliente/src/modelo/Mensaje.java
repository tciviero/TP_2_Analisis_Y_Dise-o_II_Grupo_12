package modelo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Mensaje implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String texto;
	private String emisor;
	private LocalDateTime fechaHoraEnvio;
	private boolean elMensajeEsPropio;
	
	public Mensaje(String nombreEmisor,String texto, boolean elMensajeEsPropio) {
		this.emisor=nombreEmisor;
		this.texto=texto;
		this.elMensajeEsPropio = elMensajeEsPropio;
		this.fechaHoraEnvio=LocalDateTime.now();
	}
	

	public String getText() {
		return texto;
	}
	
	
	public LocalDateTime getFechaHora() {
        return fechaHoraEnvio;
    }
	
	public String formatoFechaHora() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return fechaHoraEnvio.format(formatter);
	}

	@Override
    public String toString() {
        return "[" + formatoFechaHora() + "] " + (elMensajeEsPropio ? "Tú: " : emisor+": ")  + texto;
    }
	
	public String toStringSE() { //toString Sin Emisor
		return "[" + formatoFechaHora() + "] " + texto;
	}
	
}
