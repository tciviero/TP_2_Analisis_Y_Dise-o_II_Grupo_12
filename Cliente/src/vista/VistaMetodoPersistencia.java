package vista;

import javax.swing.JOptionPane;

public class VistaMetodoPersistencia {
	public String pedirMetodoPersistencia() {
		String[] opciones = {"json", "xml", "texto"};
		return (String) JOptionPane.showInputDialog(
				null,
				"Selecciona el método de persistencia:",
				"Método de Persistencia",
				JOptionPane.QUESTION_MESSAGE,
				null,
				opciones,
	            opciones[0]
				);
	}
}
