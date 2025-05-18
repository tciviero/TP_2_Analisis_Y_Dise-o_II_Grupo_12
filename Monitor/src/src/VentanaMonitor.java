package src.src;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.io.PrintStream;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

public class VentanaMonitor extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel monitorPane;
	
	private DefaultListModel<Servidor> modelo;
	private JList<Servidor> Lista_Servidores;
	

	public VentanaMonitor() {
		
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 480);
				
		monitorPane = new JPanel();
		monitorPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		monitorPane.setLayout(null);
		setContentPane(monitorPane);
		JPanel panel_Servidores = new JPanel();
		panel_Servidores.setLayout(null);
		panel_Servidores.setBackground(new Color(128, 128, 255));
		panel_Servidores.setBounds(10, 10, 415, 420);
		monitorPane.add(panel_Servidores);
		
		JLabel lblServidores = new JLabel("Servidores");
		lblServidores.setFont(new Font("Arial", Font.BOLD, 20));
		lblServidores.setBounds(10, 10, 200, 30);
		panel_Servidores.add(lblServidores);
		
		
		JLabel lblConsola = new JLabel("Consola:");
		lblConsola.setFont(new Font("Arial", Font.BOLD, 20));
		lblConsola.setBounds(10, 230, 200, 30);
		panel_Servidores.add(lblConsola);
		
		JTextArea consola = new JTextArea();
		consola.setEditable(false);
		consola.setLineWrap(true);
		consola.setWrapStyleWord(true);
		JScrollPane sConsola = new JScrollPane(consola);
		sConsola.setBounds(10, 269, 395, 144);
		panel_Servidores.add(sConsola);
		
		ConsoleOutputStream cos = new ConsoleOutputStream(consola);
		PrintStream ps = new PrintStream(cos);
	    System.setOut(ps);


		
		
		//listaServidores = new ArrayList<Servidor>();
		modelo = new DefaultListModel<Servidor>();
		Lista_Servidores = new JList<Servidor>(modelo);
		Lista_Servidores.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		JScrollPane sc = new JScrollPane(Lista_Servidores);
		sc.setBounds(10, 65, 395, 144);
		panel_Servidores.add(sc);
		
		/*btnIniciarConversacion = new JButton("Crear conversacion");
		btnIniciarConversacion.setBounds(220, 18, 150, 20);
		//btnAgendar.setBounds(10, 40, 90, 20);
		btnIniciarConversacion.setActionCommand("HABLAR");
		btnIniciarConversacion.setEnabled(false);
		panel_Servidores.add(btnIniciarConversacion);*/
		
	}
	
	public void addActionListener(ActionListener var1) {
		/*Teclado.addActionListener(var1);
		btnIniciarConversacion.addActionListener(var1);
		btnRegistrar.addActionListener(var1);
		btnEnviar.addActionListener(var1);
		btnIniciarSesion.addActionListener(var1);
		btnAgendar.addActionListener(var1);
		*/
	}

	public void ActualizarServidores(ArrayList<Servidor> servidores) {
		this.modelo.clear();
		
		for(Servidor s: servidores) {
			modelo.addElement(s);
		}
	}

}
