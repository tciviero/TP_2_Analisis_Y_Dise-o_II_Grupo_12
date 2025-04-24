package modelo.usuario;

public interface IFuncionalidadUsuario extends IRegistrarse, IEnviar, IRecibir, IAgendar {

	void escucharMensajes();

}
