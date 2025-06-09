package src.state;

import java.io.DataOutputStream;
import java.io.IOException;

import src.src.Monitor;

public interface EstadoMonitor {
    void manejarServidorConectado(String ip, int puerto, Monitor monitor, DataOutputStream out) throws IOException;
    void caePrimario(Monitor monitor);
    void caeSecundario(Monitor monitor);
}