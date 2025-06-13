package vista;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionListener;

import controlador.Controlador;
import modelo.Conversacion;
import modelo.Contacto.Contacto;
import modelo.usuario.Usuario;
import modelo.usuario.UsuarioYEstado;

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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JScrollPane;

public class VentanaChat extends JFrame implements IVista  {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JPanel panelVentanaChat;
	private JPanel panelVentanaContactos;
	private JPanel panelVentanaDirectorio;
	
	private JTextField NickNameUsuario;
	private JButton btnRegistrar;
	private JButton btnIniciarSesion;
	private JButton btnEnviar;
	private JButton btnAgendar;
	
	
	private ArrayList<Conversacion> listaConversaciones;
	private DefaultListModel<Conversacion> modeloConversacion;
	private JList<Conversacion> Lista_Conversacion;
	
	private ArrayList<Contacto> listaContactos;
	private DefaultListModel<Contacto> modelo;
	private JList<Contacto> Lista_Contactos;
	private JButton btnIniciarConversacion;
	private JButton btnVerContactos; 	
	private JButton btnVerUsuarios; 	
	private JButton btnVolverAChat;
	private JButton btnVolverAChatDesdeDirectorio;
	private JButton btnBuscarNickname;
	
	private JPanel panel_Chat ;
	private JLabel lblTituloChat;
	private JOptionPane opcionEncriptacion;
	private JTextArea Chat;
	private JTextField Teclado;
	private JTextField tecladoDirectorio;
	private Conversacion ConversacionAbierta;

	private ArrayList<UsuarioYEstado> Directorio;
	private DefaultListModel modeloDirectorio;
	private JList<UsuarioYEstado> Lista_Directorio;
	private JRadioButton aesButton;
	private JRadioButton blowButton;
	private AbstractButton desButton;


	
	public VentanaChat(String Ip_usuario) {
		ConversacionAbierta = null;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 355, 155);
		
		setTitle("Bienvenido");
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		setContentPane(contentPane);
		
		//panel de chat
		panelVentanaChat = new JPanel();
		panelVentanaChat.setBorder(new EmptyBorder(5, 5, 5, 5));
		panelVentanaChat.setLayout(null);
	
//------Panel Inicio----------------------------------------------------------
		JPanel panel_Inicio = new JPanel();
		panel_Inicio.setBackground(new Color(128, 128, 255));
		panel_Inicio.setBounds(10, 10, 320, 100);
		panel_Inicio.setLayout(null);
		contentPane.add(panel_Inicio);
		
		JLabel lblNickName_1 = new JLabel("NickName:");
		lblNickName_1.setBounds(10, 24, 95, 14);
		panel_Inicio.add(lblNickName_1);
		
		NickNameUsuario = new JTextField();
		NickNameUsuario.setBounds(74, 22, 86, 20);
		NickNameUsuario.setColumns(10);
		NickNameUsuario.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				if(NickNameUsuario.getText().length()>0) {
					btnRegistrar.setEnabled(true);
					btnIniciarSesion.setEnabled(true);
				}
			}
		});
		panel_Inicio.add(NickNameUsuario);
		
		JLabel lblMuestraIp = new JLabel("Tu IP:  "+Ip_usuario);
		lblMuestraIp.setBounds(10, 65, 140, 14);
		panel_Inicio.add(lblMuestraIp);
		
		
		btnRegistrar= new JButton("Registrarse");
		btnRegistrar.setActionCommand("REGISTRAR");
		btnRegistrar.setBounds(190, 20, 110, 23);
		btnRegistrar.setEnabled(false);
		panel_Inicio.add(btnRegistrar);
		
		btnIniciarSesion= new JButton("Iniciar sesión");
		btnIniciarSesion.setActionCommand("INICIAR");
		btnIniciarSesion.setBounds(190, 60, 110, 23);
		btnIniciarSesion.setEnabled(false);
		panel_Inicio.add(btnIniciarSesion);
		
		
//-------panelVentanaContactos---------------------------------------------------
		panelVentanaContactos = new JPanel();
		panelVentanaContactos.setBackground(new Color(128, 128, 255));
		panelVentanaContactos.setBounds(10, 10, 320, 100);
		panelVentanaContactos.setLayout(null);
		
//-------panelVentanaContactos---------------------------------------------------
		panelVentanaDirectorio = new JPanel();
		panelVentanaDirectorio.setBackground(new Color(128, 128, 255));
		panelVentanaDirectorio.setBounds(10, 10, 320, 100);
		panelVentanaDirectorio.setLayout(null);
		

		
//-------panel_Contactos---------------------------------------------------------
		JPanel panel_Contactos = new JPanel();
		panel_Contactos.setLayout(null);
		panel_Contactos.setBackground(new Color(128, 128, 255));
		panel_Contactos.setBounds(10, 10, 200, 640);
		//contentPane.add(panel_Contactos);
		panelVentanaContactos.add(panel_Contactos);
		
		JLabel lblContactos = new JLabel("Contactos");
		lblContactos.setFont(new Font("Arial", Font.BOLD, 20));
		lblContactos.setBounds(10, 10, 200, 30);
		
		panel_Contactos.add(lblContactos);
		
		
		listaContactos = new ArrayList<Contacto>();
		modelo = new DefaultListModel<Contacto>();
		Lista_Contactos = new JList<Contacto>(modelo);
		Lista_Contactos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		Lista_Contactos.addListSelectionListener(e->{
			if(!e.getValueIsAdjusting()) { 
				//Aca se solto el click cuando se selecciona un elemento
				Contacto c = Lista_Contactos.getSelectedValue();
				if(c!=null) {
					//System.out.println("Se selecciono a "+c);
					if(Usuario.getInstancia().ExisteConversacion(c.getNickName())) {
						//Si la conversacion ya fue creada entonces se abre automaticamente.
						//Cuando se selecciona en la seccion de contactos
						this.ConversacionAbierta=Usuario.getInstancia().getConversacion(c.getNickName());
						CargarChat(ConversacionAbierta);
						btnIniciarConversacion.setEnabled(false);
					}
					else {
						btnIniciarConversacion.setEnabled(true);
					}
					
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
		sc.setBounds(10, 105, 180, 525);
		panel_Contactos.add(sc);
		
		btnIniciarConversacion = new JButton("Crear conversacion");
		btnIniciarConversacion.setBounds(10, 40, 150, 20);
		//btnAgendar.setBounds(10, 40, 90, 20);
		btnIniciarConversacion.setActionCommand("HABLAR");
		btnIniciarConversacion.setEnabled(false);
		panel_Contactos.add(btnIniciarConversacion);
		
		btnVolverAChat = new JButton("Volver");
		btnVolverAChat.setBounds(10, 70, 150, 20);
		//btnAgendar.setBounds(10, 40, 90, 20);
		btnVolverAChat.setActionCommand("VOLVER");
		btnVolverAChat.setEnabled(true);
		panel_Contactos.add(btnVolverAChat);
		
//------panel_Conversaciones-----------------------------------------------
		JPanel panel_Conversaciones = new JPanel();
		panel_Conversaciones.setLayout(null);
		panel_Conversaciones.setBackground(new Color(128, 128, 255));
		panel_Conversaciones.setBounds(10, 10, 200, 640);
		panelVentanaChat.add(panel_Conversaciones);
		
		JLabel lblConversaciones = new JLabel("Conversaciones");
		lblConversaciones.setFont(new Font("Arial", Font.BOLD, 20));
		lblConversaciones.setBounds(10, 0, 200, 100);
		panel_Conversaciones.add(lblConversaciones);
		
		
		
		listaConversaciones = new ArrayList<Conversacion>();
		modeloConversacion = new DefaultListModel<>();
		Lista_Conversacion = new JList<Conversacion>(modeloConversacion);
		Lista_Conversacion.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		Lista_Conversacion.addListSelectionListener(e->{
			if(!e.getValueIsAdjusting()) { 
				//Aca se solto el click cuando se selecciona un elemento
				Conversacion c = Lista_Conversacion.getSelectedValue();
				if(c!=null) {
					//System.out.println("Se selecciono a "+c);
						this.ConversacionAbierta=c;
						CargarChat(ConversacionAbierta);
				}
			}
		});
		
		
		JScrollPane scrollConversaciones = new JScrollPane(Lista_Conversacion);
		scrollConversaciones.setBounds(10, 65, 180, 520);
		panel_Conversaciones.add(scrollConversaciones);
		
		
		btnVerContactos = new JButton("Nueva conversación");
		btnVerContactos.setBounds(10, 590, 180, 20);
		//btnAgendar.setBounds(10, 40, 90, 20);
		btnVerContactos.setActionCommand("VER CONTACTOS");
		btnVerContactos.setEnabled(true);
		panel_Conversaciones.add(btnVerContactos);
		
		
		btnVerUsuarios = new JButton("Agendar");
		btnVerUsuarios.setBounds(10, 615, 180, 20);
		//btnAgendar.setBounds(10, 40, 90, 20);
		btnVerUsuarios.setActionCommand("BUSCAR USUARIOS");
		btnVerUsuarios.setEnabled(true);
		panel_Conversaciones.add(btnVerUsuarios);
		
		
//------panel_CHAT---------------------------------------------------------
		panel_Chat = new JPanel();
		panel_Chat.setLayout(null);
		panel_Chat.setBackground(new Color(128, 128, 255));
		panel_Chat.setBounds(220, 10, 420, 640);
		//contentPane.add(panel_Chat);
		panelVentanaChat.add(panel_Chat);
		
		
		lblTituloChat = new JLabel("Chat");
		lblTituloChat.setBounds(10, 0, 400, 100);
		lblTituloChat.setFont(new Font("Arial", Font.BOLD, 20));
		panel_Chat.add(lblTituloChat);
		
	    aesButton = new JRadioButton("AES");
	    aesButton.setBackground(new Color(128, 128, 255));
	    aesButton.setBounds(160, 35, 70, 30);
        blowButton = new JRadioButton("BLOW");
        blowButton.setBackground(new Color(128, 128, 255));
        blowButton.setBounds(230, 35, 70, 30);
        desButton = new JRadioButton("DES");
        desButton.setBackground(new Color(128, 128, 255));
        desButton.setBounds(300, 35, 70, 30);
        ButtonGroup group = new ButtonGroup();
        group.add(aesButton);
        group.add(blowButton);
        group.add(desButton);
        aesButton.setSelected(true); // default

		panel_Chat.add(aesButton);
		panel_Chat.add(blowButton);
		panel_Chat.add(desButton);
		

		Teclado = new JFormattedTextField();
		Teclado.setBounds(10, 600, 320, 25);
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
		scrollPane_1.setBounds(10, 65, 400, 520);
		panel_Chat.add(scrollPane_1);
		
		btnEnviar = new JButton("Enviar");
		btnEnviar.setActionCommand("ENVIAR");
		btnEnviar.setBounds(340, 601, 70, 23);
		btnEnviar.setEnabled(false);
		panel_Chat.add(btnEnviar);
		
//Panel Directorio
		JPanel panel_Directorio = new JPanel();
		panel_Directorio.setLayout(null);
		panel_Directorio.setBackground(new Color(128, 128, 255));
		panel_Directorio.setBounds(10, 10, 200, 640);
		panelVentanaDirectorio.add(panel_Directorio);
		
		
		
		JLabel lblDirectorio = new JLabel("Directorio");
		lblDirectorio.setFont(new Font("Arial", Font.BOLD, 20));
		lblDirectorio.setBounds(10, 10, 180, 30);
		panel_Directorio.add(lblDirectorio);
		
		Directorio = new ArrayList<UsuarioYEstado>();
		modeloDirectorio = new DefaultListModel<UsuarioYEstado>();
		Lista_Directorio = new JList<UsuarioYEstado>(modeloDirectorio);
		Lista_Directorio.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		JScrollPane sc_1 = new JScrollPane(Lista_Directorio);
		sc_1.setBounds(10, 110, 190, 520);
		panel_Directorio.add(sc_1);
		
		btnAgendar = new JButton("Agendar");
		btnAgendar.setEnabled(true);
		btnAgendar.setActionCommand("AGENDAR");
		btnAgendar.setBounds(10, 80, 90, 20);
		panel_Directorio.add(btnAgendar);
		
		btnVolverAChatDesdeDirectorio = new JButton("Volver");
		btnVolverAChatDesdeDirectorio.setEnabled(true);
		btnVolverAChatDesdeDirectorio.setActionCommand("VOLVER");
		btnVolverAChatDesdeDirectorio.setBounds(110, 80, 90, 20);
		panel_Directorio.add(btnVolverAChatDesdeDirectorio);
		
		btnBuscarNickname = new JButton("Buscar");
		btnBuscarNickname.setEnabled(true);
		btnBuscarNickname.setActionCommand("BUSCAR NICKNAME");
		btnBuscarNickname.setBounds(110, 45, 90, 20);
		panel_Directorio.add(btnBuscarNickname);
		
		
		tecladoDirectorio = new JFormattedTextField();
		tecladoDirectorio.setBounds(20, 50, 90, 25);
		panelVentanaDirectorio.add(tecladoDirectorio);

		this.setVisible(true);
		
		this.addWindowListener(new WindowAdapter() {
		    @Override
		    public void windowClosing(WindowEvent e) {
		    	try {
		            Controlador.getInstance().notificarDesconectado();
		        } catch (Exception ex) {
		            ex.printStackTrace();
		        }
		        System.exit(0);
		    }
		});

	}


	public String getNickNameUsuarioText() {
		return NickNameUsuario.getText();
	}


	public String getTecladoText() {
		return this.Teclado.getText();
	}
	
	public void setTecladoText(String text) {
		this.Teclado.setText(text);
	}
	
//----------LISTENERS-----------------
	@Override
	public void addActionListener(ActionListener var1) {
		Teclado.addActionListener(var1);
		btnIniciarConversacion.addActionListener(var1);
		btnRegistrar.addActionListener(var1);
		btnEnviar.addActionListener(var1);
		btnIniciarSesion.addActionListener(var1);
		btnAgendar.addActionListener(var1);
		btnVerContactos.addActionListener(var1);
		btnVerUsuarios.addActionListener(var1);
		btnVolverAChat.addActionListener(var1);
		btnVolverAChatDesdeDirectorio.addActionListener(var1);
		btnBuscarNickname.addActionListener(var1);
	}

	@Override
	public void addListSelectionListener(ListSelectionListener var1) {
		Lista_Conversacion.addListSelectionListener(var1);
	}

//Parte de Inicio-registro-conexion
	public void conectado() {
		setTitle("Usuario: " + getNickNameUsuarioText());
		setContentPane(panelVentanaChat);
		setBounds(50, 50, 660, 695);
		revalidate();
	}

//-----------DIRECOTORIO-----------
	@Override
	public UsuarioYEstado getUsuarioSeleccionado() {
		return Lista_Directorio.getSelectedValue();
	}

	@Override
	public void ActualizarDirectorio(ArrayList<UsuarioYEstado> directorio) {
		// TODO Auto-generated method stub
		this.modeloDirectorio.clear();
		for(UsuarioYEstado s:directorio) {
			modeloDirectorio.addElement(s);
		}
	}

//-----------AGENDA-----------
	public Contacto getContactoSeleccionado() {
		return Lista_Contactos.getSelectedValue();
	}

	@Override
	public void ActualizaListaContactos() {
		this.modelo.clear();
		for(Contacto c:Usuario.getInstancia().getContactos()) {
			modelo.addElement(c);
		}
	}
	
	@Override
	public void ContactoSeleccionadoEsChat() {
		//Al contacto seleccionado
		//Conversacion Abierta es la conversacion del contacto seleccionado
		ConversacionAbierta=Usuario.getInstancia().getConversacion(getContactoSeleccionado().getNickName());
	}

//-----------Conversaciones-----------
	@Override
	public Conversacion getConversacionAbierta() {
		return ConversacionAbierta;
	}
	
	@Override
	public Conversacion getConversacionSelected() {
		return Lista_Conversacion.getSelectedValue();
	}

	
	@Override
	public void ActualizarListaConversaciones() {
		//El boton hablar llama a este metodo para Actualizar el JList donde se ven las conversaciones
		this.modeloConversacion.clear();
		for (Conversacion c: Usuario.getInstancia().getConversaciones()) {
			modeloConversacion.addElement(c);
		}
	}
	
	@Override
	public void OnNuevoMensajeRecibido() {
		//NOTIFICACION DE NUEVO MENSAJE RECIBIDO
		ActualizarListaConversaciones();
		if(ConversacionAbierta != null) {
			ConversacionAbierta.SetCantidadMensajesSinLeer(0);
			CargarChat(ConversacionAbierta);
		}
	}
	
//-----------CHAT-----------	
	
/*	public void CargarChat(String mensajes) {
		this.Chat.setText(mensajes);
		this.panel_Chat.setVisible(true);
		this.lblTituloChat.setText("Chat de "+ConversacionAbierta.getNickName());
	}
	*/
	@Override
	public void CargarChat(Conversacion c) {
		this.Chat.setText(c.mostrarMensajes());
		this.panel_Chat.setVisible(true);
		this.lblTituloChat.setText("Chat de "+c.getNickName());
		c.SetCantidadMensajesSinLeer(0);
		ActualizarListaConversaciones();
	}

//--------NOTIFICACIONES-----------------
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
	public void OnNuevoNotificacion(String mensaje) {
		new JNotification(mensaje);
	}

	@Override
	public void onFalloUsuarioConSesionActiva(String nickname) {
		new JNotification("El usuario " + nickname + " ya tiene una sesion activa.");
	}
	
	@Override
	public void onFalloConectarServidor() {
		new JNotification("No se pudo conectar al servidor");
	}


	@Override
	public void onFalloUsuarioNoRegistrado(String nickname) {
		new JNotification("El usuario " + nickname + " no fue registrado.");
	}


	@Override
	public void buscarUsuarios() {
		setContentPane(panelVentanaDirectorio);
		setBounds(50,50,240,695);
		revalidate();
	}

	@Override
	public void verContactos() {
		setContentPane(panelVentanaContactos);
		setBounds(50,50,240,695);
		revalidate();	
	}

	@Override
	public void volverAChat() {
		conectado();
	}

	@Override
	public String getSearchText() {
		return this.tecladoDirectorio.getText();
	}

	@Override
	public void onUsuarioAgendadoExitosamente() {
		new JNotification("Usuario agendando!");
	}

	@Override
	public String getOpcionEncriptacionText() {
		//Devuelve que opcion de encriptacion se elegio
		if(aesButton.isSelected()) {
			return "AES";
		}
		if(blowButton.isSelected()) {
			return "BLOW";
		}
		if(desButton.isSelected()) {
			return "DES";
		}
		return null;
	}	

}
