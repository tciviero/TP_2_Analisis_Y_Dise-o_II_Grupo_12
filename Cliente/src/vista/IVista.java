package vista;

import java.awt.event.ActionListener;

import javax.swing.event.ListSelectionListener;

import modelo.Conversacion;
import modelo.Contacto.Contacto;
import modelo.usuario.UsuarioYEstado;

public interface IVista extends INotificable{
	void addActionListener(ActionListener var1);
	void addListSelectionListener(ListSelectionListener var1);
	
	String getSearchText();
	String getNickNameUsuarioText();
	String getTecladoText();
	String getPuertoUsuarioText();
	void setTecladoText(String text);
	
	public UsuarioYEstado getUsuarioSeleccionado();

	Contacto getContactoSeleccionado();
	void ActualizaListaContactos();
	void ContactoSeleccionadoEsChat();

	Conversacion getConversacionSelected();
	void ActualizarListaConversaciones();
	
	Conversacion getConversacionAbierta();
	void CargarChat(Conversacion c);

	void volverAChat();
	
	void buscarUsuarios();
	void verContactos();
	void OnRegistroContactoExitoso();
	void OnFalloEnvioMensaje();
	void onFalloPuertoYaEnUso();
	void onFalloPuertoFueraRango();
	void onFalloPuertoSinUso();
	void onUsuarioAgendadoExitosamente();

	void onFalloUsuarioConSesionActiva(String nickname);
	void onFalloConectarServidor();
	void onFalloUsuarioNoRegistrado(String nickname);

}
