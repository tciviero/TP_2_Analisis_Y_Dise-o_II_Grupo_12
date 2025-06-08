package modelo.usuario;

import java.io.IOException;

import excepciones.NicknameYaRegistradoException;

public interface IRegistrarse {
	void Registrarse(String nickname) throws NicknameYaRegistradoException ;
}
