package vista;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

public class VentanaDirectorio implements IVentanaDirectorio {
	
	/*public void mostrarDirectorio(String[] usuarios_registrados, ResultadoSeleccion callback) {
	    JList<String> lista = new JList<>(usuarios_registrados);
	    lista.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

	    JButton boton = new JButton("Agendar seleccionados");

	    boton.addActionListener(e -> {
	        List<String> seleccionados = lista.getSelectedValuesList();

	        if (seleccionados.isEmpty()) {
	            JOptionPane.showMessageDialog(null, "No se seleccionó ningún usuario.");
	        } else {
	            ArrayList<String> usuario_seleccionados = new ArrayList<>(seleccionados);
	            callback.procesar(usuario_seleccionados); // Acá se devuelve el ArrayList
	            //SwingUtilities.getWindowAncestor(boton).dispose();
	        }
	    });

	    JPanel panel = new JPanel(new BorderLayout());
	    panel.add(new JScrollPane(lista), BorderLayout.CENTER);
	    panel.add(boton, BorderLayout.SOUTH);

	    JFrame frame = new JFrame("Seleccionar usuarios para agendar");
	    //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.setSize(300, 300);
	    frame.setLocationRelativeTo(null);
	    frame.add(panel);
	    frame.setVisible(true);
	}*/
	
	
	public void mostrarDirectorio(String[] usuarios, Consumer<List<String>> onAceptar, Runnable onCerrar) {
	    JList<String> lista = new JList<>(usuarios);
	    lista.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

	    JFrame frame = new JFrame("Seleccionar usuarios para agendar");
	    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	    frame.setSize(300, 200);

	    JButton boton = new JButton("Aceptar");
	    boton.addActionListener(e -> {
	        List<String> seleccionados = lista.getSelectedValuesList();
	        onAceptar.accept(seleccionados);
	        frame.dispose(); // cerrar ventana
	    });

	    frame.add(new JScrollPane(lista), BorderLayout.CENTER);
	    frame.add(boton, BorderLayout.SOUTH);

	    //cierre
	    frame.addWindowListener(new WindowAdapter() {
	        @Override
	        public void windowClosing(WindowEvent e) {
	            onCerrar.run();
	        }

	        @Override
	        public void windowClosed(WindowEvent e) {
	            onCerrar.run();
	        }
	    });

	    frame.setVisible(true);
	}

}
