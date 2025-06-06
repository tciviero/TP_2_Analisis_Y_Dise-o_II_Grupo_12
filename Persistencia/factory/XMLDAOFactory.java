package factory;
import implementaciones.MensajeDAO;
import implementaciones.MensajeXMLDAO;

public class XMLDAOFactory implements DAOFactory {
	public MensajeDAO crearMensajeDAO(String nombreUsuario) {
		return new MensajeXMLDAO(nombreUsuario);
	}
}