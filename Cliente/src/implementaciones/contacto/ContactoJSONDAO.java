package implementaciones.contacto;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import modelo.Contacto.Contacto;
import modelo_factory.MensajeFactory;

public class ContactoJSONDAO implements ContactoDAO {
	private String ARCHIVO;
	    
	public ContactoJSONDAO(String nombreUsuario) {
		this.ARCHIVO = "contactos_json_" + nombreUsuario;
	}
	
	@Override
	public void guardarContacto(Contacto contacto) {
		try {
			JSONArray contactosArray;
			// si existe se lee, si no se crea uno nuevo
			if (new File(ARCHIVO).exists()) {
				String contenido = new String(Files.readAllBytes(Paths.get(ARCHIVO)));
				contactosArray = new JSONArray(contenido);
			} else {
				contactosArray = new JSONArray();
			}

			JSONObject nuevoContacto = new JSONObject();
			nuevoContacto.put("nickname", contacto.getNickName());

			contactosArray.put(nuevoContacto);

			try (FileWriter writer = new FileWriter(ARCHIVO)) {
				writer.write(contactosArray.toString(4));
			}
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
		}

		@Override
		public List<Contacto> cargarContactos(String miUsuario) {
			List<Contacto> contactos = new ArrayList<>();

	        try {
	            if (!new File(ARCHIVO).exists()) return contactos;

	            String contenido = new String(Files.readAllBytes(Paths.get(ARCHIVO)));
	            JSONArray contactosArray = new JSONArray(contenido);

	            for (int i = 0; i < contactosArray.length(); i++) {
	                JSONObject msgObj = contactosArray.getJSONObject(i);

	                String nickname_contacto = msgObj.getString("nickname");

	                contactos.add(new Contacto(nickname_contacto));
	            }

	        } catch (Exception e) {
	            e.printStackTrace();
	        }

	        return contactos;
		}
}
