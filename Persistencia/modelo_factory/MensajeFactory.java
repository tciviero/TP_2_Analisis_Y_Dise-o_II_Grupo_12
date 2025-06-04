package modelo_factory;

import java.time.LocalDateTime;

public class MensajeFactory {
	private String contenido;
	private String fecha;
	private String emisor;
	private String receptor;

	public MensajeFactory(String contenido, String emisor, String receptor) {
		this.contenido = contenido;
		this.fecha = LocalDateTime.now().toString();
		this.emisor = emisor;
		this.receptor = receptor;
	}

	public MensajeFactory() {
	}

	public String getContenido() {
		return this.contenido;
	}

	public String getFecha() {
		return this.fecha;
	}

	public String getEmisor() {
		return this.emisor;
	}

	public String getReceptor() {
		return this.receptor;
	}

}
