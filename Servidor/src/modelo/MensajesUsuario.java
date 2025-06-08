package modelo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import controlador.ControladorServidor;

public class MensajesUsuario {
	private static MensajesUsuario instance=null;
	
	private int cont=0;
	
	private HashMap<String, HashMap<String, List<MensajePendiente>>> mensajes;
	
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
        	if(receptor.equals(RRecept)) {
        		HashMap<String, List<MensajePendiente>> emisores = mensajes.get(receptor);
        		for (String emisor : emisores.keySet()) {
        			for (MensajePendiente mensaje : emisores.get(emisor)) {
        				i++;
        			}
        		}
        	}
        }
    	return i;
    }

    public void agregarMensaje(String receptor, String emisor, String mensaje, String algoritmoEncriptacion) {
        mensajes.putIfAbsent(receptor, new HashMap<>());
        HashMap<String, List<MensajePendiente>> mensajesDeEmisores = mensajes.get(receptor);
        mensajesDeEmisores.putIfAbsent(emisor, new ArrayList<MensajePendiente>());
        mensajesDeEmisores.get(emisor).add(new MensajePendiente(mensaje, algoritmoEncriptacion));
    }
    
    public String historial_mensajes_recibidos(String receptor) {
    	String respuesta = "HISTORIAL";
    	String aux = "";
    	int i = 0;
    	HashMap<String, List<MensajePendiente>> emisores = mensajes.get(receptor);
        if(emisores!=null) {
        	for (String emisor : emisores.keySet()) {
                //System.out.println("  De " + emisor + ":");
                for (MensajePendiente mensaje : emisores.get(emisor)) {
                    i++;
                	System.out.println("mensaje de: " + emisor + " : " + mensaje.getTexto() + " encriptado con: " + mensaje.getAlgoritmoEncriptacion());
                    aux += "`" + emisor + "`" + mensaje.getTexto() + "`" + mensaje.getAlgoritmoEncriptacion();
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
    	for (int i = 1; i < dataArray.length; i += 4) {
            String receptor = dataArray[i];
            String emisor = dataArray[i + 1];
            String mensaje = dataArray[i + 2];
            String algoritmoEncriptacion = dataArray[i + 3];

            // Llamar al método para agregar el mensaje
            //System.out.println("Se agrega ["+receptor+","+emisor+","+mensaje+"]a lista de mensajes");
            this.agregarMensaje(receptor, emisor, mensaje, algoritmoEncriptacion);
        }
    }
    
    public void mostrarMensajes() {
    	this.cont++;
    	for (String receptor : mensajes.keySet()) {
            System.out.println("Mensajes para " + receptor + ":");
            HashMap<String, List<MensajePendiente>> emisores = mensajes.get(receptor);
            for (String emisor : emisores.keySet()) {
                System.out.println("  De " + emisor + ":");
                for (MensajePendiente mensaje : emisores.get(emisor)) {
                    System.out.println("    - " + mensaje.getTexto());
                }
            }
        }
    }
    
    public String getTodosMensajesFormateado() {
    	String MensajesPendientesCompleto="MENSAJES_PENDIENTES";//`RecPrueba`EmiPrueba`msgmsgmsg";
        for (String receptor : mensajes.keySet()) {
            HashMap<String, List<MensajePendiente>> emisores = mensajes.get(receptor);
            for (String emisor : emisores.keySet()) {
                for (MensajePendiente mensaje : emisores.get(emisor)) {
                	MensajesPendientesCompleto += "`" + receptor + "`" + emisor+"`" + mensaje.getTexto() + "`" + mensaje.getAlgoritmoEncriptacion();
                }
            }
        }
        return MensajesPendientesCompleto;
    }
}
