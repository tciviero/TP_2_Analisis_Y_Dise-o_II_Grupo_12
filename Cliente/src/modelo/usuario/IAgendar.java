package modelo.usuario;

import java.io.IOException;

import excepciones.VentanaCerradaSinSeleccionadosException;
import modelo.Contacto.Contacto;

public interface IAgendar {
	void agendarContacto() throws VentanaCerradaSinSeleccionadosException,IOException;
	boolean esContacto(String nickname);
}
