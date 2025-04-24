package modelo.usuario;

import java.io.IOException;

import excepciones.VentanaCerradaSinSeleccionadosException;

public interface IRecibir {
	void NuevoMensajeRecibido(String[] arrayMensaje) throws VentanaCerradaSinSeleccionadosException;
}
