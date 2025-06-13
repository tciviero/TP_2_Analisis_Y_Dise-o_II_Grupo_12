package implementaciones.mensaje;
import java.util.List;

import modelo_factory.MensajeFactory;

public interface MensajeDAO {
	void guardarMensaje(MensajeFactory mensaje);
	List<MensajeFactory> cargarMensajes(String miUsuario);
}
