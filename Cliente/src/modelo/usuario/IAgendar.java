package modelo.usuario;

import modelo.Contacto.Contacto;

public interface IAgendar {
	void agendarContacto(Contacto contacto);
	boolean EsContacto(String iP_Receptor, int puerto_Receptor);
}
