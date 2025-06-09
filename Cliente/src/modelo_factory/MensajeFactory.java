package modelo_factory;

import java.time.LocalDateTime;

public class MensajeFactory {
	private String contenido;
	private String fecha;
	private String emisor;
	private String receptor;
	private String metodo;

	public MensajeFactory(String contenido, String emisor, String receptor, String metodo) {
		this.contenido = contenido;
		this.fecha = LocalDateTime.now().toString();
		this.emisor = emisor;
		this.receptor = receptor;
		this.metodo = metodo;
	}
	
	public MensajeFactory(String contenido, String hora, String emisor, String receptor, String metodo) {
		this.contenido = contenido;
		this.fecha = hora;
		this.emisor = emisor;
		this.receptor = receptor;
		this.metodo = metodo;
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
	
	public String getMetodo() {
		return this.metodo;
	}

}
