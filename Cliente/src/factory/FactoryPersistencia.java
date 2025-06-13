package factory;

import factory.implementaciones.JSONPersistenciaFactory;
import factory.implementaciones.TextoPersistenciaFactory;
import factory.implementaciones.XMLPersistenciaFactory;

public class FactoryPersistencia {
	public static PersistenciaFactory crearFactory(String tipo) {
        switch (tipo.toLowerCase()) {
            case "texto": return new TextoPersistenciaFactory();
            case "json": return new JSONPersistenciaFactory();
            case "xml": return new XMLPersistenciaFactory();
            default: throw new IllegalArgumentException("Formato no soportado");
        }
    }
	/*public static MensajeDAO crearPersistenciaDAO(String tipo, String nombreUsuario) {
		switch (tipo.toLowerCase()) {
		case "texto":
			return new MensajeTextoPlanoDAO(nombreUsuario);
		case "json":
			return new MensajeJSONDAO(nombreUsuario);
		case "xml":
			return new MensajeXMLDAO(nombreUsuario);
		default:
			throw new IllegalArgumentException("Formato de persistencia no soportado: " + tipo);
		}
	}
	public static ContactoDAO crearContactoDAO(String tipo, String nombreUsuario) {
		switch (tipo.toLowerCase()) {
		case "texto":
			return new ContactoTextoPlanoDAO(nombreUsuario);
		case "json":
			return new ContactoJSONDAO(nombreUsuario);
		case "xml":
			return new ContactoXMLDAO(nombreUsuario);
		default:
			throw new IllegalArgumentException("Formato de persistencia no soportado: " + tipo);
		}
	}*/
}
