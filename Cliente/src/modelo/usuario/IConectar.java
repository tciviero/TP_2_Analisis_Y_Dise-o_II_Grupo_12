package modelo.usuario;

import java.io.IOException;

public interface IConectar {
	void conectar(String nombre, String IP) throws IOException;
}
