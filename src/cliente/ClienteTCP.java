package cliente;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * TODO: Complementa esta clase para que genere la conexi�n TCP con el servidor
 * para enviar un boleto, recibir la respuesta y finalizar la sesion
 */
public class ClienteTCP {

	// Constructor del cliente

	// String donde se almacena la direccion IP
	private String ip;
	// int donde se almacena el puerto
	private int puerto;
	// creación del socket
	private Socket socket;
	// creacion de BufferedReader y PrintWriter
	private PrintWriter salida;
	private BufferedReader entrada;

	public ClienteTCP(String ip, int puerto) {
		// recibe la ip y el puerto por parametro
		this.ip = ip;
		this.puerto = puerto;
		try {
			// inicializacion del socket, bufferedReader y PrintWriter
			socket = new Socket(ip, puerto);
			salida = new PrintWriter(this.socket.getOutputStream(), true);
			entrada = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
		} catch (IOException e) {
			System.err.println("Error al conectar con el servidor");
		}
	}

	/**
	 * Metodo para enviar la combinacion al cliente y recibir la respuesta de
	 * cuantos numeros acierta
	 * 
	 * @param combinacion que se desea enviar
	 * @return respuesta del servidor con la respuesta del boleto
	 */
	public String comprobarBoleto(int[] combinacion) {

		// StringBuilder para crear el mensaje con la combinación que va a enviar el
		// cliente

		StringBuilder mensaje = new StringBuilder();
		// for each que recorre la combinacion y agrega los numeros con un espacio al
		// StringBuilder
		for (int numero : combinacion) {
			mensaje.append(numero).append(" ");
		}
		// envia el mensaje al servidor con PrintWriter
		salida.println(mensaje.toString());

		try {
			// recibe el mensaje del servidor con BufferedReader
			return entrada.readLine();
		} catch (IOException e) {
			return "Error";
		}

	}

	/**
	 * Sirve para finalizar la la conexi�n de Cliente y Servidor
	 */
	public void finSesion() {

		try {
			// manda con PrintWriter FIN al cliente
			salida.println("FIN");
			// cierre del socket,bufferedReader y PrintWriter
			socket.close();
			salida.close();
			entrada.close();
			System.out.println(" -> Cliente Finalizado");
		} catch (IOException e) {
			System.out.println("Error");
		}

	}

}
