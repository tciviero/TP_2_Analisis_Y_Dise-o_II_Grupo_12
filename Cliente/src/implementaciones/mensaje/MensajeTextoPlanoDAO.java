package implementaciones.mensaje;
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

	private String ARCHIVO;
	
	public MensajeTextoPlanoDAO(String nombreUsuario) {
		this.ARCHIVO = "historial_texto_" + nombreUsuario;
	}

	@Override
	public void guardarMensaje(MensajeFactory mensaje) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARCHIVO, true))) {
			String linea = mensaje.getFecha() + "|" + mensaje.getEmisor() + "|" + mensaje.getReceptor() + "|"
					+ mensaje.getContenido() + "|" + mensaje.getMetodo();
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
				String[] partes = linea.split("\\|", 5);
				if (partes.length == 5) {
					String fecha = partes[0];
					String emisor = partes[1];
					String receptor = partes[2];
					String contenido = partes[3];
					String metodo = partes[4];

					if (emisor.equals(miUsuario) || receptor.equals(miUsuario)) {
						mensajes.add(new MensajeFactory(contenido, emisor, receptor,metodo));
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return mensajes;
	}
}