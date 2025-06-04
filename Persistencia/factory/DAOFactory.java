package factory;
import implementaciones.MensajeDAO;

public interface DAOFactory {
	MensajeDAO crearMensajeDAO();
}