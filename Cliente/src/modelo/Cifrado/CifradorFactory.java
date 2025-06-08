package modelo.Cifrado;

public class CifradorFactory {
	
	private static CifradorFactory instance = null;
	
	private CifradorFactory() {
		
	}
	
	public static CifradorFactory getInstance() {
		if(instance == null) {
			instance = new CifradorFactory();
		}
		return instance;
	}
	
	public ICifrador getCifrador(String nombreCifrador) {
		if (nombreCifrador.equalsIgnoreCase("AES")){
			return new CifradorAES();
		}
		return null;
	}
}
