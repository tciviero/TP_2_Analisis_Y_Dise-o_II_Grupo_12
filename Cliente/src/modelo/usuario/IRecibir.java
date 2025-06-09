package modelo.usuario;

public interface IRecibir {
	void NuevoMensajeRecibido(String Emisor,String texto);
	void NuevoMensajeRecibido(String Emisor,String Receptor, String texto);
}
