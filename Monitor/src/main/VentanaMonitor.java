package main;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

public class VentanaMonitor extends JFrame {

	private static final long serialVersionUID = 1L;
	//private JPanel contentPane;
	private JPanel monitorPane;
	
	//private ArrayList<Servidor> listaServidores;
	private DefaultListModel<Servidor> modelo;
	private JList<Servidor> Lista_Servidores;
	private JButton btnIniciarConversacion;
	
	/**
	 * Launch the application.
	 
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					VentanaMonitor frame = new VentanaMonitor();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	  *  Create the frame.
	  */

	public VentanaMonitor() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 280);
				
		monitorPane = new JPanel();
		monitorPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		monitorPane.setLayout(null);
		setContentPane(monitorPane);
		JPanel panel_Servidores = new JPanel();
		panel_Servidores.setLayout(null);
		panel_Servidores.setBackground(new Color(128, 128, 255));
		panel_Servidores.setBounds(10, 10, 415, 220);
		monitorPane.add(panel_Servidores);
		
		JLabel lblServidores = new JLabel("Servidores");
		lblServidores.setFont(new Font("Arial", Font.BOLD, 20));
		lblServidores.setBounds(10, 10, 200, 30);
		panel_Servidores.add(lblServidores);
		
		
		//listaServidores = new ArrayList<Servidor>();
		modelo = new DefaultListModel<Servidor>();
		Lista_Servidores = new JList<Servidor>(modelo);
		Lista_Servidores.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		Lista_Servidores.addListSelectionListener(e->{
			if(!e.getValueIsAdjusting()) { 
				//Aca se solto el click cuando se selecciona un elemento
				Servidor c = Lista_Servidores.getSelectedValue();
				if(c!=null) {
					//Se selecciono el servidor c, se podria 
					//System.out.println("Se selecciono a "+c);
					//this.ConversacionAbierta=Usuario.getInstancia().getConversacion(c.getNickName());
					//CargarChat(ConversacionAbierta);
					btnIniciarConversacion.setEnabled(false);
					btnIniciarConversacion.setEnabled(true);
					
				}
				else {//Aca no hay nadie seleccionado
					btnIniciarConversacion.setEnabled(false);
				}
			}
			else {
				//El click esta presionado sobre un elemento
				//System.out.println("El click se ha presionado en un elemento");
			}
		});
		JScrollPane sc = new JScrollPane(Lista_Servidores);
		sc.setBounds(10, 65, 395, 144);
		panel_Servidores.add(sc);
		
		btnIniciarConversacion = new JButton("Crear conversacion");
		btnIniciarConversacion.setBounds(220, 18, 150, 20);
		//btnAgendar.setBounds(10, 40, 90, 20);
		btnIniciarConversacion.setActionCommand("HABLAR");
		btnIniciarConversacion.setEnabled(false);
		panel_Servidores.add(btnIniciarConversacion);
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
