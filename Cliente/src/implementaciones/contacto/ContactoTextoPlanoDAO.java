package implementaciones.contacto;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import modelo.Contacto.Contacto;
import modelo_factory.MensajeFactory;

public class ContactoTextoPlanoDAO implements ContactoDAO {

private String ARCHIVO;
	
	public ContactoTextoPlanoDAO(String nombreUsuario) {
		this.ARCHIVO = "contactos_texto_" + nombreUsuario;
	}

	@Override
	public void guardarContacto(Contacto contacto) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARCHIVO, true))) {
			String linea = contacto.getNickName();
			writer.write(linea);
			writer.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<Contacto> cargarContactos(String miUsuario) {
		List<Contacto> contactos = new ArrayList<>();
		File archivo = new File(ARCHIVO);
		if (!archivo.exists())
			return contactos;

		try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
			String linea;
			while ((linea = reader.readLine()) != null) {
				contactos.add(new Contacto(linea));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return contactos;
	}
}
