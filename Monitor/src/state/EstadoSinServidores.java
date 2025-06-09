package src.state;

import java.io.DataOutputStream;
import java.io.IOException;

import src.src.Monitor;

public class EstadoSinServidores implements EstadoMonitor {
    public void manejarServidorConectado(String ip, int puerto, Monitor monitor, DataOutputStream out) throws IOException {
        monitor.setearPrimario(ip, puerto);
        out.writeUTF("sos_primario");
        monitor.iniciarHiloMonitoreoPrimario();
        monitor.setEstado(new EstadoSoloPrimario());
    }

	@Override
	public void caePrimario(Monitor monitor) {
		//no pasa nada
	}

	@Override
	public void caeSecundario(Monitor monitor) {
		//no pasa nada
	}
}