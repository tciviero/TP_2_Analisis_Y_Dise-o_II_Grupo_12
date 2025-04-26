package vista;

import java.util.ArrayList;

import modelo.usuario.UsuarioYEstado;


public interface INotificable {
	void OnNuevoMensajeRecibido();

	void ActualizarDirectorio(ArrayList<UsuarioYEstado> directorio);
	void conectado();
	void OnNuevoNotificacion(String mensaje);


}
