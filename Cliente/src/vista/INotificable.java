package vista;

import java.util.ArrayList;

public interface INotificable {
	void OnNuevoMensajeRecibido();

	void ActualizarDirectorio(ArrayList<String> directorio);
}
