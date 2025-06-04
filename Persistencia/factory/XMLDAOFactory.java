package factory;
import implementaciones.MensajeDAO;
import implementaciones.MensajeXMLDAO;

public class XMLDAOFactory implements DAOFactory {
	public MensajeDAO crearMensajeDAO() {
		return new MensajeXMLDAO();
	}
}