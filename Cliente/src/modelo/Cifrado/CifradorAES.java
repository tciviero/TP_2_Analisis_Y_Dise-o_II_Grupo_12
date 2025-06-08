package modelo.Cifrado;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class CifradorAES implements ICifrador{

	@Override
	public String cifrarMensaje(String mensaje, String clave) throws Exception{

        byte[] llaveBytes = clave.getBytes(StandardCharsets.UTF_8);

        llaveBytes = Arrays.copyOf(llaveBytes, 16); // Pads with zeroes if too short
        
        SecretKeySpec llavePadeadaA128Bits = new SecretKeySpec(llaveBytes, "AES");

        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        
        cipher.init(Cipher.ENCRYPT_MODE, llavePadeadaA128Bits);
        byte[] encriptacion = cipher.doFinal(mensaje.getBytes(StandardCharsets.UTF_8));
        String mensajeEncriptado = Base64.getEncoder().encodeToString(encriptacion);
        
		return mensajeEncriptado;
	}

	@Override
	public String descifrarMensaje(String mensajeEncriptado, String clave) throws Exception {
		byte[] llaveBytes = clave.getBytes(StandardCharsets.UTF_8);

        llaveBytes = Arrays.copyOf(llaveBytes, 16); // Pads with zeroes if too short
        
        SecretKeySpec llavePadeadaA128Bits = new SecretKeySpec(llaveBytes, "AES");

        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        
        cipher.init(Cipher.DECRYPT_MODE, llavePadeadaA128Bits);
        byte[] descifrado = cipher.doFinal(Base64.getDecoder().decode(mensajeEncriptado));

		return new String(descifrado, StandardCharsets.UTF_8);
	}


}
