package vista;

import java.util.ArrayList;

public interface IVentanaDirectorio {
	@FunctionalInterface
	public interface ResultadoSeleccion {
	    void procesar(ArrayList<String> seleccionados);
	}

}
