package modelo.Cifrado;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class CifradorBlowfish implements ICifrador {

	@Override
	public String cifrarMensaje(String mensaje, String clave) throws Exception {
		byte[] claveBytes = clave.getBytes("UTF-8");
		byte[] claveBytesPadeada = new byte[56];

	    for (int i = 0; i < 56; i++) {
	    	claveBytesPadeada[i] = (i < claveBytes.length) ? claveBytes[i] : 0;
	    }
		
        SecretKeySpec clavePadeada = new SecretKeySpec(claveBytesPadeada, "Blowfish");

        Cipher cipher = Cipher.getInstance("Blowfish");
        cipher.init(Cipher.ENCRYPT_MODE, clavePadeada);

        byte[] mensajeEncriptado = cipher.doFinal(mensaje.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(mensajeEncriptado);
	}

	@Override
	public String descifrarMensaje(String mensaje, String clave) throws Exception {
		byte[] claveBytes = clave.getBytes("UTF-8");
		byte[] claveBytesPadeada = new byte[56];
		
		for (int i = 0; i < 56; i++) {
	    	claveBytesPadeada[i] = (i < claveBytes.length) ? claveBytes[i] : 0;
	    }
        SecretKeySpec clavePadeada = new SecretKeySpec(claveBytesPadeada, "Blowfish");
        Cipher cipher = Cipher.getInstance("Blowfish");
        cipher.init(Cipher.DECRYPT_MODE, clavePadeada);

        byte[] mensajeDecifrado = cipher.doFinal(Base64.getDecoder().decode(mensaje));
        return new String(mensajeDecifrado, "UTF-8");
	}

}
