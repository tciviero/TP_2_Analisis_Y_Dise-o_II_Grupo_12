package excepciones;

public class UsuarioNoRegistradoException extends Exception {
	public UsuarioNoRegistradoException(String mensaje) {
		super(mensaje);
	}
}
