package factory;

import implementaciones.MensajeDAO;
import implementaciones.MensajeTextoPlanoDAO;

public class TextoPlanoDAOFactory implements DAOFactory {
	public MensajeDAO crearMensajeDAO() {
		return new MensajeTextoPlanoDAO();
	}
}