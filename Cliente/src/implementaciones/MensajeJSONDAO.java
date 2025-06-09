package implementaciones;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import modelo_factory.MensajeFactory;

public class MensajeJSONDAO implements MensajeDAO {
    private String ARCHIVO;
    
    public MensajeJSONDAO(String nombreUsuario) {
		this.ARCHIVO = "historial_json_" + nombreUsuario;
	}

	@Override
	public void guardarMensaje(MensajeFactory mensaje) {
		try {
            JSONArray mensajesArray;

            // si existe se lee, si no se crea uno nuevo
            if (new File(ARCHIVO).exists()) {
                String contenido = new String(Files.readAllBytes(Paths.get(ARCHIVO)));
                mensajesArray = new JSONArray(contenido);
            } else {
                mensajesArray = new JSONArray();
            }

            JSONObject nuevoMensaje = new JSONObject();
            nuevoMensaje.put("emisor", mensaje.getEmisor());
            nuevoMensaje.put("receptor", mensaje.getReceptor());
            nuevoMensaje.put("contenido", mensaje.getContenido());
            nuevoMensaje.put("metodo", mensaje.getMetodo());

            String fechaStr = mensaje.getFecha();
            LocalDateTime dateTime = LocalDateTime.parse(fechaStr);
            String hora = dateTime.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            nuevoMensaje.put("hora", hora);

            mensajesArray.put(nuevoMensaje);

            try (FileWriter writer = new FileWriter(ARCHIVO)) {
                writer.write(mensajesArray.toString(4));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
	}

	@Override
	public List<MensajeFactory> cargarMensajes(String miUsuario) {
		List<MensajeFactory> mensajes = new ArrayList<>();

        try {
            if (!new File(ARCHIVO).exists()) return mensajes;

            String contenido = new String(Files.readAllBytes(Paths.get(ARCHIVO)));
            JSONArray mensajesArray = new JSONArray(contenido);

            for (int i = 0; i < mensajesArray.length(); i++) {
                JSONObject msgObj = mensajesArray.getJSONObject(i);

                String emisor = msgObj.getString("emisor");
                String receptor = msgObj.getString("receptor");

                if (!emisor.equals(miUsuario) && !receptor.equals(miUsuario)) continue;

                String contenidoMensaje = msgObj.getString("contenido");
                String hora = msgObj.getString("hora");
                String metodo = msgObj.getString("metodo");

                mensajes.add(new MensajeFactory(contenidoMensaje, hora, emisor, receptor,metodo));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return mensajes;
	}

}
