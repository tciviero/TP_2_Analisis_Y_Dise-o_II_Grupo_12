package factory.implementaciones;

import factory.PersistenciaFactory;
import implementaciones.contacto.ContactoDAO;
import implementaciones.contacto.ContactoJSONDAO;
import implementaciones.mensaje.MensajeDAO;
import implementaciones.mensaje.MensajeJSONDAO;

public class JSONPersistenciaFactory implements PersistenciaFactory {
	public MensajeDAO crearMensajeDAO(String usuario) {
        return new MensajeJSONDAO(usuario);
    }
    public ContactoDAO crearContactoDAO(String usuario) {
        return new ContactoJSONDAO(usuario);
    }
}
