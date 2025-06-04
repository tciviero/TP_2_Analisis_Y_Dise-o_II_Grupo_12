package factory;

import implementaciones.MensajeDAO;
import implementaciones.MensajeJSONDAO;
import implementaciones.MensajeTextoPlanoDAO;
import implementaciones.MensajeXMLDAO;

public class FactoryPersistencia {
	public static MensajeDAO crearDAO(String tipo) {
		switch (tipo.toLowerCase()) {
		case "texto_plano":
			return new MensajeTextoPlanoDAO();
		case "json":
			return new MensajeJSONDAO();
		case "xml":
			return new MensajeXMLDAO();
		default:
			throw new IllegalArgumentException("Formato de persistencia no soportado: " + tipo);
		}
	}
}
