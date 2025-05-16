package modelo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MensajesUsuario {
    private HashMap<String, HashMap<String, List<String>>> mensajes;
	
    public MensajesUsuario() {
        this.mensajes = new HashMap<>();
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
        	respuesta += "`" + i + aux;
        }else
        	respuesta = "no_tuvo";
        return respuesta;
    }
    
    public void eliminarMensajesYaLeidos(String nickname) {
    	mensajes.remove(nickname);
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
}
