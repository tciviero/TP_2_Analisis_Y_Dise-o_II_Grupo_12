package factory;

import implementaciones.contacto.ContactoDAO;
import implementaciones.mensaje.MensajeDAO;

public interface PersistenciaFactory {
	MensajeDAO crearMensajeDAO(String usuario);
    ContactoDAO crearContactoDAO(String usuario);
}
