package factory.implementaciones;

import factory.PersistenciaFactory;
import implementaciones.contacto.ContactoDAO;
import implementaciones.contacto.ContactoXMLDAO;
import implementaciones.mensaje.MensajeDAO;
import implementaciones.mensaje.MensajeXMLDAO;

public class XMLPersistenciaFactory implements PersistenciaFactory {
	public MensajeDAO crearMensajeDAO(String usuario) {
        return new MensajeXMLDAO(usuario);
    }
    public ContactoDAO crearContactoDAO(String usuario) {
        return new ContactoXMLDAO(usuario);
    }
}
