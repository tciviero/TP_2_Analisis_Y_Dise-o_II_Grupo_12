package factory;

import implementaciones.MensajeDAO;
import implementaciones.MensajeJSONDAO;
import implementaciones.MensajeTextoPlanoDAO;
import implementaciones.MensajeXMLDAO;

public class FactoryPersistencia {
	public static MensajeDAO crearDAO(String tipo, String nombreUsuario) {
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
}
