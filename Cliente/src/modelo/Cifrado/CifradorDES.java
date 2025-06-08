package modelo.Cifrado;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class CifradorDES implements ICifrador {

	@Override
	public String cifrarMensaje(String mensaje, String clave) throws Exception {
		byte[] claveBytes = clave.getBytes("UTF-8");
	    byte[] clavePadeada = new byte[8];
	    for (int i = 0; i < 8; i++) {
	    	clavePadeada[i] = (i < claveBytes.length) ? claveBytes[i] : 0;
	    }
		
		
        SecretKeySpec secretKey = new SecretKeySpec(clavePadeada, "DES");

        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        byte[] mensajeCifrado = cipher.doFinal(mensaje.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(mensajeCifrado);
	}

	@Override
	public String descifrarMensaje(String mensaje, String clave) throws Exception {
		byte[] claveBytes = clave.getBytes("UTF-8");
	    byte[] clavePadeada = new byte[8];
	    for (int i = 0; i < 8; i++) {
	    	clavePadeada[i] = (i < claveBytes.length) ? claveBytes[i] : 0;
	    }
		
		
        SecretKeySpec secretKey = new SecretKeySpec(clavePadeada, "DES");

        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        byte[] mensajeDecifrado = cipher.doFinal(Base64.getDecoder().decode(mensaje));
        return new String(mensajeDecifrado, "UTF-8");
	}

}
