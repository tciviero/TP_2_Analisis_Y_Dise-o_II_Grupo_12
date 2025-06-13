package implementaciones.mensaje;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import modelo.Mensaje;
import modelo_factory.MensajeFactory;

public class MensajeXMLDAO implements MensajeDAO {
	
	private String ARCHIVO;
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
	
	public MensajeXMLDAO(String nombreUsuario) {
		this.ARCHIVO = "historial_xml_" + nombreUsuario;
	}

	@Override
	public void guardarMensaje(MensajeFactory mensaje) {
		try {
            File archivo = new File(ARCHIVO);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc;

            //si existe lo leemos, si no lo creamos
            if (archivo.exists()) {
                doc = builder.parse(archivo);
            } else {
                doc = builder.newDocument();
                Element root = doc.createElement("mensajes_xml");
                doc.appendChild(root);
            }

            Element mensajeElem = doc.createElement("mensaje");

            Element emisor = doc.createElement("emisor");
            emisor.setTextContent(mensaje.getEmisor());
            mensajeElem.appendChild(emisor);

            Element receptor = doc.createElement("receptor");
            receptor.setTextContent(mensaje.getReceptor());
            mensajeElem.appendChild(receptor);

            Element contenido = doc.createElement("contenido");
            contenido.setTextContent(mensaje.getContenido());
            mensajeElem.appendChild(contenido);

            Element hora_elemento = doc.createElement("hora");
            String fechaStr = mensaje.getFecha();
            LocalDateTime dateTime = LocalDateTime.parse(fechaStr);
            String hora = dateTime.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            hora_elemento.setTextContent(hora);
            mensajeElem.appendChild(hora_elemento);
            
            Element metodo = doc.createElement("metodo");
            metodo.setTextContent(mensaje.getMetodo());
            mensajeElem.appendChild(metodo);

            doc.getDocumentElement().appendChild(mensajeElem);

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(doc), new StreamResult(new FileOutputStream(ARCHIVO)));

        } catch (Exception e) {
            e.printStackTrace();
        }
	}

	@Override
	public List<MensajeFactory> cargarMensajes(String miUsuario) {
		List<MensajeFactory> mensajes = new ArrayList<>();
        try {
            File archivo = new File(ARCHIVO);
            if (!archivo.exists()) return mensajes;

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(archivo);

            NodeList nodos = doc.getElementsByTagName("mensaje");

            for (int i = 0; i < nodos.getLength(); i++) {
                Element elem = (Element) nodos.item(i);

                String contenido = elem.getElementsByTagName("contenido").item(0).getTextContent();
                
                String emisor = elem.getElementsByTagName("emisor").item(0).getTextContent();
                String receptor = elem.getElementsByTagName("receptor").item(0).getTextContent();

                if (!emisor.equals(miUsuario) && !receptor.equals(miUsuario)) continue;

                String hora = elem.getElementsByTagName("hora").item(0).getTextContent();
                //LocalDateTime fecha = LocalDateTime.parse(fechaStr, formatter);
                
                String metodo = elem.getElementsByTagName("metodo").item(0).getTextContent();

                mensajes.add(new MensajeFactory(contenido,hora,emisor, receptor,metodo));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return mensajes;
	}

}
