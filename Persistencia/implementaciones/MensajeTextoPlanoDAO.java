package implementaciones;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import modelo_factory.MensajeFactory;

public class MensajeTextoPlanoDAO implements MensajeDAO {

	private static final String ARCHIVO = "mensajes_texto_plano.txt";

	@Override
	public void guardarMensaje(MensajeFactory mensaje) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARCHIVO, true))) {
			String linea = mensaje.getFecha() + "|" + mensaje.getEmisor() + "|" + mensaje.getReceptor() + "|"
					+ mensaje.getContenido();
			writer.write(linea);
			writer.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<MensajeFactory> cargarMensajes(String miUsuario) {
		List<MensajeFactory> mensajes = new ArrayList<>();
		File archivo = new File(ARCHIVO);
		if (!archivo.exists())
			return mensajes;

		try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
			String linea;
			while ((linea = reader.readLine()) != null) {
				String[] partes = linea.split("\\|", 4);
				if (partes.length == 4) {
					String fecha = partes[0];
					String emisor = partes[1];
					String receptor = partes[2];
					String contenido = partes[3];

					if (emisor.equals(miUsuario) || receptor.equals(miUsuario)) {
						mensajes.add(new MensajeFactory(contenido, emisor, receptor));
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return mensajes;
	}
}