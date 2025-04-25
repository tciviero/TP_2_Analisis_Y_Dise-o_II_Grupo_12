package vista;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionListener;

import modelo.Contacto.Contacto;

import modelo.usuario.Usuario;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JFormattedTextField;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.JList;
import javax.swing.DefaultListModel;
import javax.swing.JScrollPane;

public class VentanaChat extends JFrame implements IVista  {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JPanel chatPane;
	
	private JTextField NickNameUsuario;
	private JTextField PuertoUsuario;
	private JButton btnRegistrar;
	private JButton btnIniciarSesion;
	private JButton btnEnviar;
	
	
	private ArrayList<Contacto> listaConversaciones;
	private DefaultListModel<Contacto> modeloConversacion;
	private JList<Contacto> Lista_Conversacion;
	
	private ArrayList<Contacto> listaContactos;
	private DefaultListModel<Contacto> modelo;
	private JList<Contacto> Lista_Contactos;
	private JButton btnIniciarConversacion; 	
	
	private JPanel panel_Chat ;
	private JTextArea Chat;
	private JTextField Teclado;
	private Contacto contactoChatAbierto;


	
	public VentanaChat(String Ip_usuario) {
		contactoChatAbierto = null;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 355, 155);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		setContentPane(contentPane);
		
		//panel de chat
		chatPane = new JPanel();
		chatPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		chatPane.setLayout(null);
	
//------Panel Inicio----------------------------------------------------------
		JPanel panel_Inicio = new JPanel();
		panel_Inicio.setBackground(new Color(128, 128, 255));
		panel_Inicio.setBounds(10, 10, 320, 100);
		panel_Inicio.setLayout(null);
		contentPane.add(panel_Inicio);
		
		JLabel lblNickName_1 = new JLabel("NickName:");
		lblNickName_1.setBounds(10, 11, 95, 14);
		panel_Inicio.add(lblNickName_1);
		
		NickNameUsuario = new JTextField();
		NickNameUsuario.setBounds(74, 8, 86, 20);
		NickNameUsuario.setColumns(10);
		panel_Inicio.add(NickNameUsuario);
		
		JLabel lblPuerto = new JLabel("Puerto:");
		lblPuerto.setBounds(10, 65, 70, 24);
		panel_Inicio.add(lblPuerto);
		
		JLabel lblMuestraIp = new JLabel("Tu IP:  "+Ip_usuario);
		lblMuestraIp.setBounds(10, 40, 140, 14);
		panel_Inicio.add(lblMuestraIp);
		
		PuertoUsuario = new JTextField();
		PuertoUsuario.setBounds(75, 67, 86, 20);
		PuertoUsuario.setColumns(10);
		PuertoUsuario.addKeyListener(new KeyAdapter() {
			//Esto verifica que solo se ingresen numeros en el TextField de puerto
			//Si no es un numero lo borra
			//Tampoco permite ingresar más de 5 caracteres
			@Override
			public void keyTyped(KeyEvent e) {
				char c =e.getKeyChar();
				if(!Character.isDigit(c)) {
					e.consume();
				}
				if(PuertoUsuario.getText().length()>4) {
					e.consume();
				}
			}
		});
		panel_Inicio.add(PuertoUsuario);
		
		btnRegistrar= new JButton("Registrarse");
		btnRegistrar.setActionCommand("REGISTRAR");
		btnRegistrar.setBounds(190, 20, 110, 23);
		panel_Inicio.add(btnRegistrar);
		
		btnIniciarSesion= new JButton("Iniciar sesión");
		btnIniciarSesion.setActionCommand("INICIAR SESION");
		btnIniciarSesion.setBounds(190, 60, 110, 23);
		panel_Inicio.add(btnIniciarSesion);
		
//-------panel_Contactos---------------------------------------------------------
		JPanel panel_Contactos = new JPanel();
		panel_Contactos.setLayout(null);
		panel_Contactos.setBackground(new Color(128, 128, 255));
		panel_Contactos.setBounds(10, 10, 320, 640);
		//contentPane.add(panel_Contactos);
		chatPane.add(panel_Contactos);
		
		JLabel lblContactos = new JLabel("Contactos");
		lblContactos.setFont(new Font("Arial", Font.BOLD, 20));
		lblContactos.setBounds(10, 10, 200, 30);
		panel_Contactos.add(lblContactos);
		
		
		listaContactos = new ArrayList<Contacto>();
		modelo = new DefaultListModel<>();
		Lista_Contactos = new JList<Contacto>(modelo);
		Lista_Contactos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		Lista_Contactos.addListSelectionListener(e->{
			if(!e.getValueIsAdjusting()) { 
				//Aca se solto el click cuando se selecciona un elemento
				Contacto c = Lista_Contactos.getSelectedValue();
				if(c!=null) {
					//System.out.println("Se selecciono a "+c);
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
		JScrollPane sc = new JScrollPane(Lista_Contactos);
		sc.setBounds(10, 65, 300, 565);
		panel_Contactos.add(sc);
		
		btnIniciarConversacion = new JButton("Hablar");
		btnIniciarConversacion.setBounds(10, 40, 75, 20);
		btnIniciarConversacion.setActionCommand("HABLAR");
		btnIniciarConversacion.setEnabled(true);
		panel_Contactos.add(btnIniciarConversacion);
		
//------panel_Conversaciones-----------------------------------------------
		JPanel panel_Conversaciones = new JPanel();
		panel_Conversaciones.setLayout(null);
		panel_Conversaciones.setBackground(new Color(128, 128, 255));
		panel_Conversaciones.setBounds(330, 10, 320, 640);
		chatPane.add(panel_Conversaciones);
		
		JLabel lblConversaciones = new JLabel("Conversaciones");
		lblConversaciones.setFont(new Font("Arial", Font.BOLD, 20));
		lblConversaciones.setBounds(10, 10, 200, 30);
		panel_Conversaciones.add(lblConversaciones);
		
		
		
		listaConversaciones = new ArrayList<Contacto>();
		modeloConversacion = new DefaultListModel<>();
		Lista_Conversacion = new JList<Contacto>(modeloConversacion);
		Lista_Conversacion.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		JScrollPane scrollConversaciones = new JScrollPane(Lista_Conversacion);
		scrollConversaciones.setBounds(10, 65, 300, 565);
		panel_Conversaciones.add(scrollConversaciones);
		
		
//------panel_CHAT---------------------------------------------------------
		panel_Chat = new JPanel();
		panel_Chat.setLayout(null);
		panel_Chat.setBackground(new Color(128, 128, 255));
		panel_Chat.setBounds(665, 10, 810, 640);
		//contentPane.add(panel_Chat);
		chatPane.add(panel_Chat);
		
		
		JLabel lblTituloChat = new JLabel("Chat con {NICKNAME_DE_CONTACTO_CON_LA_CONVER_CLICKEADA}");
		lblTituloChat.setBounds(10, 0, 1000, 100);
		lblTituloChat.setFont(new Font("Arial", Font.BOLD, 20));
		panel_Chat.add(lblTituloChat);

		Teclado = new JFormattedTextField();
		Teclado.setBounds(10, 600, 700, 25);
		panel_Chat.add(Teclado);
		String DefaultText="Escribe aqui un mensaje";
		Teclado.setText(DefaultText);
		Teclado.setForeground(Color.GRAY);
		FocusListener FOCUS = new FocusListener() {
			//Todo esto es para que aparezca un texto por defecto en el teclado
            @Override
            public void focusGained(FocusEvent e) {
                // Si el texto predeterminado está presente, lo borra cuando el usuario hace clic
                if (Teclado.getText().equals(DefaultText)) {
                	Teclado.setText("");  // Limpiar el texto
                	Teclado.setForeground(Color.BLACK);  // Establecer el color de texto normal
                	btnEnviar.setEnabled(true);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                // Si el campo está vacío después de que pierde el foco, restaurar el texto predeterminado
                if (Teclado.getText().isEmpty()) {
                	Teclado.setText(DefaultText);
                	Teclado.setForeground(Color.GRAY);  // Volver al color gris
                	btnEnviar.setEnabled(false);
                }
            }
        };
        Teclado.addFocusListener(FOCUS);
		Teclado.setActionCommand("ENVIAR");
		
		Chat = new JTextArea(18, 20);
		Chat.setEditable(false);
		Chat.setLineWrap(true);   // Habilitar el ajuste de línea
		Chat.setWrapStyleWord(true); // Ajustar palabras completas (en lugar de cortar palabras por la mitad)
        

		JScrollPane scrollPane_1 = new JScrollPane(Chat);
		scrollPane_1.setBounds(10, 65, 790, 520);
		panel_Chat.add(scrollPane_1);
		
		btnEnviar = new JButton("Enviar");
		btnEnviar.setActionCommand("ENVIAR");
		btnEnviar.setBounds(720, 601, 70, 23);
		btnEnviar.setEnabled(false);
		panel_Chat.add(btnEnviar);
		
		this.setVisible(true);
	}


	public String getNickNameUsuarioText() {
		return NickNameUsuario.getText();
	}


	public String getPuertoUsuarioText() {
		return PuertoUsuario.getText();
	}

	
	public Contacto getContactoSeleccionado() {
		return Lista_Contactos.getSelectedValue();
	}
	
	public String getTecladoText() {
		return this.Teclado.getText();
	}
	public void setTecladoText(String text) {
		this.Teclado.setText(text);
	}
	
	public void conectado() {
		setContentPane(chatPane);
		setBounds(100, 100, 1500, 700);
		revalidate();
	}
	
	public void ActualizaListaConversaciones() {
		//El boton agregar contacto llama a este metodo para Actualizar el JList donde se ven los contactos
		this.modeloConversacion.clear();
		for (Contacto c: Usuario.getInstancia().getConversaciones()) {
			modeloConversacion.addElement(c);

		}
	}




	public void CargarChat(String mensajes) {
		this.Chat.setText(mensajes);
		this.panel_Chat.setVisible(true);
	}


	@Override
	public void addActionListener(ActionListener var1) {
		Teclado.addActionListener(var1);
		btnIniciarConversacion.addActionListener(var1);
		btnRegistrar.addActionListener(var1);
		btnEnviar.addActionListener(var1);
	}


	@Override
	public void addListSelectionListener(ListSelectionListener var1) {
		Lista_Conversacion.addListSelectionListener(var1);
	}

	@Override
	public void OnNuevoMensajeRecibido() {
		//NOTIFICACION DE NUEVO MENSAJE RECIBIDO
		ActualizaListaConversaciones();
		if(contactoChatAbierto != null) {
			contactoChatAbierto.SetCantidadMensajesSinLeer(0);
			CargarChat(contactoChatAbierto.mostrarMensajes());
		}
	}

	
	

	@Override
	public void OnFalloEnvioMensaje() {
		new JNotification("Error de conexión! Reintente");
		
	}
	
	@Override
	public void onFalloPuertoYaEnUso() {
		new JNotification("Puerto ya en uso. Ingrese otro.");
	}
	
	@Override
	public void onFalloPuertoFueraRango() {
		new JNotification("Puerto fuera de rango.");
	}
	
	@Override
	public void onFalloPuertoSinUso() {
		new JNotification("No hay usuario utilizando esa direccion.");
	}

	@Override
	public void OnRegistroContactoExitoso() {
		new JNotification("Contacto registrado exitosamente!");
	}


	@Override
	public void ContactoSeleccionadoEsChat() {
		contactoChatAbierto = getContactoSeleccionado();
	}

	@Override
	public Contacto getContactoChat() {
		return contactoChatAbierto;
	}


	@Override
	public Contacto getConversacionSelected() {
		return Lista_Conversacion.getSelectedValue();
	}


	@Override
	public void ActualizaListaContactos() {
		// TODO Auto-generated method stub
		
	}







}
