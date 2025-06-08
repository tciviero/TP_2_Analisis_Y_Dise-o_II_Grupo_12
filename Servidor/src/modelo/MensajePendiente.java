package modelo;

public class MensajePendiente {
	private String texto;
	private String algoritmoEncriptacion;
	
	public MensajePendiente(String texto, String algoritmoEncriptacion) {
		this.texto = texto;
		this.algoritmoEncriptacion = algoritmoEncriptacion;
	}
	
	
	public String getTexto() {
		return texto;
	}
	public String getAlgoritmoEncriptacion() {
		return algoritmoEncriptacion;
	}
}
