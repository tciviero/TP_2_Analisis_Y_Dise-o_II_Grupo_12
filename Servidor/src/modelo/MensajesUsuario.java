package modelo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import controlador.ControladorServidor;

public class MensajesUsuario {
	private static MensajesUsuario instance=null;
	
	
	private HashMap<String, HashMap<String, List<String>>> mensajes;
	
    private MensajesUsuario() {
        this.mensajes = new HashMap<>();
    }
    
    public static MensajesUsuario getInstance() {
		if(instance==null) {
			instance = new MensajesUsuario();
		}
		return instance;
	}
    
    public int CantMensajesPendientes(String RRecept) {
        int i=0;
        for (String receptor : mensajes.keySet()) {
            HashMap<String, List<String>> emisores = mensajes.get(receptor);
            for (String emisor : emisores.keySet()) {
                for (String mensaje : emisores.get(emisor)) {
                    i++;
                }
            }
        }
    	return i;
    }

    public void agregarMensaje(String receptor, String emisor, String mensaje) {
        mensajes.putIfAbsent(receptor, new HashMap<>());
        HashMap<String, List<String>> mensajesDeEmisores = mensajes.get(receptor);
        mensajesDeEmisores.putIfAbsent(emisor, new ArrayList<>());
        mensajesDeEmisores.get(emisor).add(mensaje);
    }
    
    public String historial_mensajes_recibidos(String receptor) {
    	String respuesta = "HISTORIAL";
    	String aux = "";
    	int i = 0;
    	HashMap<String, List<String>> emisores = mensajes.get(receptor);
        if(emisores!=null) {
        	for (String emisor : emisores.keySet()) {
                //System.out.println("  De " + emisor + ":");
                for (String mensaje : emisores.get(emisor)) {
                    i++;
                	System.out.println("mensaje de: " + emisor + " : " + mensaje);
                    aux += "`" + emisor + "`" + mensaje;
                }
            }
        	// respuesta= "HISTORIAL`cantMensajes`emisor`mensaje`...."
        	respuesta += "`" + i + aux;
        }else
        	respuesta = "no_tuvo";
        return respuesta;
    }
    
    public void eliminarMensajesYaLeidos(String nickname) {
    	mensajes.remove(nickname);
    }
    
    public void CargarHashMap(String MensajesPendientesCompleto) {
    	//Es llamado cuando se reciben los mensajes pendientes formateados
    	//Es para iniciar al servidor cuando es secundario.
    	this.mensajes.clear();
    	//"MENSAJES_PENDIENTES"+"receptor"+"emisor"+"mensaje"
    	String[] dataArray = MensajesPendientesCompleto.split("`");
    	
    	for (int i = 1; i < dataArray.length; i += 3) {
            String receptor = dataArray[i];
            String emisor = dataArray[i + 1];
            String mensaje = dataArray[i + 2];

            // Llamar al método para agregar el mensaje
            //System.out.println("Se agrega ["+receptor+","+emisor+","+mensaje+"]a lista de mensajes");
            this.agregarMensaje(receptor, emisor, mensaje);
        }
    }
    
    public void mostrarMensajes() {
        for (String receptor : mensajes.keySet()) {
            System.out.println("Mensajes para " + receptor + ":");
            HashMap<String, List<String>> emisores = mensajes.get(receptor);
            for (String emisor : emisores.keySet()) {
                System.out.println("  De " + emisor + ":");
                for (String mensaje : emisores.get(emisor)) {
                    System.out.println("    - " + mensaje);
                }
            }
        }
    }
    
    public String getTodosMensajesFormateado() {
    	String MensajesPendientesCompleto="MENSAJES_PENDIENTES";//`RecPrueba`EmiPrueba`msgmsgmsg";
        for (String receptor : mensajes.keySet()) {
            HashMap<String, List<String>> emisores = mensajes.get(receptor);
            for (String emisor : emisores.keySet()) {
                for (String mensaje : emisores.get(emisor)) {
                	MensajesPendientesCompleto+="`" +receptor+"`" + emisor+"`" + mensaje;
                }
            }
        }
        return MensajesPendientesCompleto;
    }
}
