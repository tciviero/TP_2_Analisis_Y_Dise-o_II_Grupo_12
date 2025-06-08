package factory;
import implementaciones.MensajeDAO;
import implementaciones.MensajeJSONDAO;

public class JSONDAOFactory implements DAOFactory {
	public MensajeDAO crearMensajeDAO(String nombreUsuario) {
		return new MensajeJSONDAO(nombreUsuario);
	}
}