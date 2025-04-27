package excepciones;

public class UsuarioConSesionActivaException extends Exception {
	public UsuarioConSesionActivaException (String mensaje) {
		super(mensaje);
	}
}
