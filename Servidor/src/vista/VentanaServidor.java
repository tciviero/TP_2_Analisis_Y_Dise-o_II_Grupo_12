package vista;


import java.awt.Color;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

import modelo.Usuario;
import modelo.Contacto.Contacto;


public class VentanaServidor extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private ArrayList<String> listaUsuarios;//del panel de directorio
	private DefaultListModel<String> modelo;
	private JList<String> ListaUsuarios;

	
	public VentanaServidor(String Ip_Servidor, int Puerto_Servidor) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 350, 700);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		this.setVisible(true);
		
		JPanel panel_Inicio = new JPanel();
		panel_Inicio.setBackground(new Color(128, 128, 255));
		panel_Inicio.setBounds(10, 10, 300, 80);
		panel_Inicio.setLayout(null);
		contentPane.add(panel_Inicio);
		
		JLabel lblPuerto = new JLabel("Puerto Servidor:"+Puerto_Servidor);
		lblPuerto.setBounds(10, 45, 280, 24);
		panel_Inicio.add(lblPuerto);
		
		JLabel lblMuestraIp = new JLabel("IP Servidor:  "+Ip_Servidor);
		lblMuestraIp.setBounds(10, 10, 280, 24);
		panel_Inicio.add(lblMuestraIp);
		
		//Panel Directorio
		JPanel panel_Directorio = new JPanel();
		panel_Directorio.setLayout(null);
		panel_Directorio.setBackground(new Color(128, 128, 255));
		panel_Directorio.setBounds(10, 100, 300, 550);
		contentPane.add(panel_Directorio);
		
		JLabel lblUsuarios = new JLabel("Usuarios/Directorio:");
		lblUsuarios.setBounds(10, 10, 280, 24);
		panel_Directorio.add(lblUsuarios);
		
		listaUsuarios= new ArrayList<String>();
		modelo = new DefaultListModel<>();
        for (String c : listaUsuarios) {
            modelo.addElement(c);
        }
        ListaUsuarios = new JList<String>(modelo);
        ListaUsuarios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		ListaUsuarios.addListSelectionListener(e->{
			if(!e.getValueIsAdjusting()) { 
				//Aca se solto el click cuando se selecciona un elemento
				String u =null;
				u = ListaUsuarios.getSelectedValue();
				if(u!=null) {
					//System.out.println("Se selecciono a "+c);
					//btnHablar.setEnabled(true);
				}
				else {//Aca no hay nadie seleccionado
					//btnHablar.setEnabled(false);
				}
			}
			else {
				//El click esta presionado sobre un elemento
				//System.out.println("El click se ha presionado en un elemento");
			}
		});
		JScrollPane sc = new JScrollPane(ListaUsuarios);
		sc.setBounds(10, 40, 280, 500);
		panel_Directorio.add(sc);
		
	}


	public void ActualizarDirectorio(String[] usuarios) {
		this.modelo.clear();
		for (String c: usuarios) {
			modelo.addElement(c);
		}
	}
	
	

}
