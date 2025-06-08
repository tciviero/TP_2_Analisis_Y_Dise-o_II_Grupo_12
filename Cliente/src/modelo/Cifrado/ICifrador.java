package modelo.Cifrado;

public interface ICifrador {
	String cifrarMensaje(String mensaje, String clave) throws Exception;
	String descifrarMensaje(String mensaje, String clave) throws Exception;
}
