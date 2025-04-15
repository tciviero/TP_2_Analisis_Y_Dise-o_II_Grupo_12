package modelo.usuario;

import java.io.IOException;

import modelo.Contacto.Contacto;

public interface IEnviar {
	void Envia(Contacto destinatario, String texto) throws IOException;
}
