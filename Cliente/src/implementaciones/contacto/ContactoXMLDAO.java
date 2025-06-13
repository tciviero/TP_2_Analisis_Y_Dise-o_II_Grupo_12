package implementaciones.contacto;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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

import modelo.Contacto.Contacto;
import modelo_factory.MensajeFactory;

public class ContactoXMLDAO implements ContactoDAO {
	private String ARCHIVO;
    
	public ContactoXMLDAO(String nombreUsuario) {
		this.ARCHIVO = "contactos_xml_" + nombreUsuario;
	}
	
	@Override
	public void guardarContacto(Contacto contacto) {
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
                Element root = doc.createElement("contactos_xml");
                doc.appendChild(root);
            }

            Element contactoElem = doc.createElement("contacto");

            Element nickname_contacto = doc.createElement("nickname");
            nickname_contacto.setTextContent(contacto.getNickName());
            contactoElem.appendChild(nickname_contacto);

            doc.getDocumentElement().appendChild(contactoElem);

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(doc), new StreamResult(new FileOutputStream(ARCHIVO)));

        } catch (Exception e) {
            e.printStackTrace();
        }
	}

	@Override
	public List<Contacto> cargarContactos(String miUsuario) {
		List<Contacto> contactos = new ArrayList<>();
        try {
            File archivo = new File(ARCHIVO);
            if (!archivo.exists()) return contactos;

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(archivo);

            NodeList nodos = doc.getElementsByTagName("contacto");

            for (int i = 0; i < nodos.getLength(); i++) {
                Element elem = (Element) nodos.item(i);

                String nickname_contacto = elem.getElementsByTagName("nickname").item(0).getTextContent();

                contactos.add(new Contacto(nickname_contacto));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return contactos;
	}

}
