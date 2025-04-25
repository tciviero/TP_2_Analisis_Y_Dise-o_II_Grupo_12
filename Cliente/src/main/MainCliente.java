package main;

import java.io.IOException;

import controlador.Controlador;

public class MainCliente {

	public static void main(String[] args) throws IOException {
		Controlador controlador = new Controlador();
		controlador.Iniciar();
	}

}
