package vista;

import java.awt.event.ActionListener;
import java.io.IOException;

import modelo.Contacto.Contacto;

public interface IVista extends INotificable{
	void addActionListener(ActionListener var1);
	String getPuertoContactoText();
	String getIpContactoText();
	String getNombreContactoText();
	String getPuertoUsuarioText();
	String getNickNameUsuarioText();
	String getTecladoText();
	void setTecladoText(String text);
	void CargarChat(String mensajes);
	Contacto getContactoSeleccionado();
	void ActualizaListaContactos() throws IOException;
	void conectado();
	void ContactoSeleccionadoEsChat();
	Contacto getContactoChat();
	void OnRegistroContactoExitoso();
	void OnFalloEnvioMensaje();
	void onFalloPuertoYaEnUso();
	void onFalloPuertoFueraRango();
	void onFalloPuertoSinUso();
	void onFalloNicknameYaRegistrado();
}
