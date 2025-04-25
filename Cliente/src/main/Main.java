package main;

import controlador.Controlador;


//BOTON REGISTRARSE PUERTO 10
//BOTON INICIAR SESION PUERTO 11
//BOTON ENVIAR MENSAJE PUERTO 12
public class Main {

	public static void main(String[] args) {
		Controlador controlador = new Controlador();
		controlador.Iniciar();
	}

}
