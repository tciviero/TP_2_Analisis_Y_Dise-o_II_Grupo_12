package factory;

import implementaciones.MensajeDAO;
import implementaciones.MensajeTextoPlanoDAO;

public class TextoPlanoDAOFactory implements DAOFactory {
	public MensajeDAO crearMensajeDAO(String nombreUsuario) {
		return new MensajeTextoPlanoDAO(nombreUsuario);
	}
}