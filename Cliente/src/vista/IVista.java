package vista;

import java.awt.event.ActionListener;

import javax.swing.event.ListSelectionListener;

import modelo.Contacto.Contacto;
import modelo.usuario.UsuarioYEstado;

public interface IVista extends INotificable{
	void addActionListener(ActionListener var1);
	void addListSelectionListener(ListSelectionListener var1);
	String getNickNameUsuarioText();
	String getTecladoText();
	void setTecladoText(String text);
	void CargarChat(String mensajes);
	Contacto getContactoSeleccionado();
	void ActualizaListaContactos();
	void ContactoSeleccionadoEsChat();
	Contacto getContactoChat();
	void OnRegistroContactoExitoso();
	void OnFalloEnvioMensaje();
	void onFalloPuertoYaEnUso();
	void onFalloPuertoFueraRango();
	void onFalloPuertoSinUso();
	void onFalloUsuarioConSesionActiva(String nickname);
	void onFalloUsuarioNoRegistrado(String nickname);
	Contacto getConversacionSelected();
	String getPuertoUsuarioText();
	public UsuarioYEstado getUsuarioSeleccionado();
}
