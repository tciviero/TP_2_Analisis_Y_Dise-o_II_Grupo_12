package state;

import java.io.DataOutputStream;
import java.io.IOException;

import modelo.Monitor;

public class EstadoSoloPrimario implements EstadoMonitor {
    public void manejarServidorConectado(String ip, int puerto, Monitor monitor, DataOutputStream out) throws IOException {
        monitor.setearSecundario(ip, puerto);
        out.writeUTF("sos_secundario");
        monitor.sincronizarPrimarioConSecundario();
        monitor.iniciarHiloMonitoreoSecundario();
        monitor.setEstado(new EstadoAmbosServidores());
    }

	@Override
	public void caePrimario(Monitor monitor) {
		monitor.setEstado(new EstadoSinServidores());
	}

	@Override
	public void caeSecundario(Monitor monitor) {
		//no pasa nada
	}
}