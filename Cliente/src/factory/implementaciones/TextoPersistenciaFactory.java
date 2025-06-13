package factory.implementaciones;

import factory.PersistenciaFactory;
import implementaciones.contacto.ContactoDAO;
import implementaciones.contacto.ContactoTextoPlanoDAO;
import implementaciones.mensaje.MensajeDAO;
import implementaciones.mensaje.MensajeTextoPlanoDAO;

public class TextoPersistenciaFactory implements PersistenciaFactory {
	public MensajeDAO crearMensajeDAO(String usuario) {
        return new MensajeTextoPlanoDAO(usuario);
    }
    public ContactoDAO crearContactoDAO(String usuario) {
        return new ContactoTextoPlanoDAO(usuario);
    }
}
