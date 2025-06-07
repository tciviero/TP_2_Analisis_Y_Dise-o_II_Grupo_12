package src.state;

import java.io.DataOutputStream;
import java.io.IOException;

import src.src.Monitor;

public class EstadoAmbosServidores implements EstadoMonitor {
    public void manejarServidorConectado(String ip, int puerto, Monitor monitor, DataOutputStream out) {
        try {
			out.writeUTF("ya_hay_dos_servidores");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

	@Override
	public void caePrimario(Monitor monitor) {
		monitor.promoverSecundario();
    	monitor.setEstado(new EstadoSoloPrimario());
	}

	@Override
	public void caeSecundario(Monitor monitor) {
		monitor.setearSecundarioCaido();
		monitor.setEstado(new EstadoSoloPrimario());
	}
}