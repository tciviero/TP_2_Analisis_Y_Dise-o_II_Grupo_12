package vista;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import modelo.Contacto.Contacto;
import modelo.usuario.Usuario;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JFormattedTextField;
import java.awt.Color;
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
	
	private JTextField NickNameUsuario;
	private JTextField PuertoUsuario;
	private JButton btnConectar;
	private JButton btnAgregar;
	private JButton btnEnviar;
	
	private JTextField NombreContacto;
	private JTextField IpContacto;
	private JTextField PuertoContacto;
	
	private ArrayList<Contacto> listaContactos;
	private DefaultListModel<Contacto> modelo;
	private JList<Contacto> Lista_Contactos;
	private JButton btnHablar; 
	
	
	private JPanel panel_Chat ;
	private JTextArea Chat;
	private JTextField Teclado;
	private Contacto contactoChatAbierto;


	
	public VentanaChat(String Ip_usuario) {
		contactoChatAbierto = null;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 670, 480);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		setContentPane(contentPane);
	
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
		
		btnConectar= new JButton("Conectar");
		btnConectar.setActionCommand("CONECTAR");
		btnConectar.setBounds(190, 36, 110, 23);
		panel_Inicio.add(btnConectar);
		
//--------panel_NuevoContacto-------------------------------------------------------
		JPanel panel_NuevoContacto = new JPanel();
		panel_NuevoContacto.setBackground(new Color(128, 128, 255));
		panel_NuevoContacto.setBounds(10, 120, 320, 100);
		panel_NuevoContacto.setLayout(null);
		contentPane.add(panel_NuevoContacto);
		
		JLabel lblNuevoContacto = new JLabel("Nuevo Contacto");
		lblNuevoContacto.setBounds(10, 10, 108, 14);
		panel_NuevoContacto.add(lblNuevoContacto);
		
		JLabel lblNombre = new JLabel("Nombre:");
		lblNombre.setBounds(10, 30, 60, 14);
		panel_NuevoContacto.add(lblNombre);
		
		JLabel lbl_IP = new JLabel("IP:");
		lbl_IP.setBounds(10, 50, 60, 14);
		panel_NuevoContacto.add(lbl_IP);
		
		JLabel lblPuerto_Contacto = new JLabel("Puerto:");
		lblPuerto_Contacto.setBounds(10, 70, 60, 14);
		panel_NuevoContacto.add(lblPuerto_Contacto);
		
		NombreContacto = new JTextField();
		NombreContacto.setBounds(80, 30, 100, 20);
		NombreContacto.setEnabled(false);
		NombreContacto.setColumns(10);
		panel_NuevoContacto.add(NombreContacto);
		
		IpContacto = new JTextField();
		IpContacto.setText(Ip_usuario);
		IpContacto.setColumns(10);
		IpContacto.setBounds(80, 50, 100, 20);
		IpContacto.setEnabled(false);
		IpContacto.addKeyListener(new KeyAdapter() {
			//Esto verifica que solo se ingresen numeros o puntos en el TextField de IP
			//Si no es un numero o un punto lo borra
			//Tampoco permite ingresar más de 14 caracteres
			@Override
			public void keyTyped(KeyEvent e) {
				char c =e.getKeyChar();
				if(!Character.isDigit(c) && c!='.') {
					e.consume();
				}
				if(IpContacto.getText().length()>14) {
					e.consume();
				}
			}
		});
		panel_NuevoContacto.add(IpContacto);
		
		PuertoContacto = new JTextField();
		PuertoContacto.setColumns(10);
		PuertoContacto.setBounds(80, 70, 100, 20);
		PuertoContacto.setEnabled(false);
		PuertoContacto.addKeyListener(new KeyAdapter() {
			//Esto verifica que solo se ingresen numeros en el TextField de puerto
			//Si no es un numero lo borra
			//Tampoco permite ingresar más de 5 caracteres
			@Override
			public void keyTyped(KeyEvent e) {
				char c =e.getKeyChar();
				if(!Character.isDigit(c)) {
					e.consume();
				}
				if(PuertoContacto.getText().length()>4) {
					e.consume();
				}
			}
		});
		panel_NuevoContacto.add(PuertoContacto);
		
		btnAgregar = new JButton("Agregar");
		btnAgregar.setBounds(201, 50, 89, 23);
		btnAgregar.setActionCommand("AGREGAR");
		btnAgregar.setEnabled(false);
		panel_NuevoContacto.add(btnAgregar);
		
//-------panel_Contactos---------------------------------------------------------
		JPanel panel_Contactos = new JPanel();
		panel_Contactos.setLayout(null);
		panel_Contactos.setBackground(new Color(128, 128, 255));
		panel_Contactos.setBounds(10, 230, 320, 200);
		contentPane.add(panel_Contactos);
		
		JLabel lblContactos = new JLabel("Contactos:");
		lblContactos.setBounds(10, 9, 108, 14);
		panel_Contactos.add(lblContactos);
		
		
		listaContactos= new ArrayList<Contacto>();
		modelo = new DefaultListModel<>();
        for (Contacto c : listaContactos) {
            modelo.addElement(c);
        }
		Lista_Contactos = new JList<Contacto>(modelo);
		Lista_Contactos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		Lista_Contactos.addListSelectionListener(e->{
			if(!e.getValueIsAdjusting()) { 
				//Aca se solto el click cuando se selecciona un elemento
				Contacto c = Lista_Contactos.getSelectedValue();
				if(c!=null) {
					//System.out.println("Se selecciono a "+c);
					btnHablar.setEnabled(true);
				}
				else {//Aca no hay nadie seleccionado
					btnHablar.setEnabled(false);
				}
			}
			else {
				//El click esta presionado sobre un elemento
				//System.out.println("El click se ha presionado en un elemento");
			}
		});
		JScrollPane sc = new JScrollPane(Lista_Contactos);
		sc.setBounds(10, 50, 300, 140);
		panel_Contactos.add(sc);
		
		btnHablar = new JButton("Hablar");
		btnHablar.setBounds(10, 25, 75, 20);
		btnHablar.setActionCommand("HABLAR");
		btnHablar.setEnabled(false);
		panel_Contactos.add(btnHablar);
		
//------panel_CHAT---------------------------------------------------------
		panel_Chat = new JPanel();
		panel_Chat.setLayout(null);
		panel_Chat.setBackground(new Color(128, 128, 255));
		panel_Chat.setBounds(340, 10, 300, 420);
		contentPane.add(panel_Chat);
		
		JLabel lblTituloChat = new JLabel("Chat con Contacto");
		lblTituloChat.setBounds(10, 10, 108, 14);
		panel_Chat.add(lblTituloChat);

		Teclado = new JFormattedTextField();
		Teclado.setBounds(10, 384, 200, 25);
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
		scrollPane_1.setBounds(10, 30, 280, 342);
		panel_Chat.add(scrollPane_1);
		
		btnEnviar = new JButton("Enviar");
		btnEnviar.setActionCommand("ENVIAR");
		btnEnviar.setBounds(220, 385, 70, 23);
		btnEnviar.setEnabled(false);
		panel_Chat.add(btnEnviar);
		
		panel_Chat.setVisible(false);
		this.setVisible(true);
	}


	public String getNickNameUsuarioText() {
		return NickNameUsuario.getText();
	}


	public String getPuertoUsuarioText() {
		return PuertoUsuario.getText();
	}


	public String getNombreContactoText() {
		return NombreContacto.getText();
	}


	public String getIpContactoText() {
		return IpContacto.getText();
	}


	public String getPuertoContactoText() {
		return PuertoContacto.getText();
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
		NickNameUsuario.setEnabled(false);
		PuertoUsuario.setEnabled(false);
		btnConectar.setEnabled(false);
		IpContacto.setEnabled(true);
		NombreContacto.setEnabled(true);
		PuertoContacto.setEnabled(true);
		btnAgregar.setEnabled(true);
	}
	
	public void ActualizaListaContactos() {
		//El boton agregar contacto llama a este metodo para Actualizar el JList donde se ven los contactos
		this.modelo.clear();
		for (Contacto c: Usuario.getInstancia().getContactos()) {
			modelo.addElement(c);

		}
	}




	public void CargarChat(String mensajes) {
		this.Chat.setText(mensajes);
		this.panel_Chat.setVisible(true);
	}


	@Override
	public void addActionListener(ActionListener var1) {
		Teclado.addActionListener(var1);
		btnHablar.addActionListener(var1);
		btnConectar.addActionListener(var1);
		btnAgregar.addActionListener(var1);
		btnEnviar.addActionListener(var1);
	}


	@Override
	public void OnNuevoMensajeRecibido() {
		//NOTIFICACION DE NUEVO MENSAJE RECIBIDO
		ActualizaListaContactos();
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


}
