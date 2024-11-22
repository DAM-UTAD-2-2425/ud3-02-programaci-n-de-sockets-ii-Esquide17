package servidor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

/**
 * TODO: Complementa esta clase para que acepte conexiones TCP con clientes para
 * recibir un boleto, generar la respuesta y finalizar la sesion
 */
public class ServidorTCP {
	// array que almacenara las respuesta del servidor segun el numeo de aciertos
	private String[] respuesta;
	// array para almacenar la combinacion ganadora
	private int[] combinacion;
	// variables para almacenar el reintegro y el complementario
	private int reintegro;
	private int complementario;
	// creacion del serverSocket y el socket del cliente
	private ServerSocket serverSocket;
	private Socket cliente;
	// creacion de BufferedReader y PrintWriter
	private BufferedReader entrada;
	private PrintWriter salida;
	// Array para almacenar la combinación recibida del cliente
	private int[] combinacionCliente;

	public ServidorTCP(int puerto) {
		this.respuesta = new String[9];
		this.respuesta[0] = "Boleto inv�lido - N�meros repetidos";
		this.respuesta[1] = "Boleto inv�lido - n�meros incorretos (1-49)";
		this.respuesta[2] = "6 aciertos";
		this.respuesta[3] = "5 aciertos + complementario";
		this.respuesta[4] = "5 aciertos";
		this.respuesta[5] = "4 aciertos";
		this.respuesta[6] = "3 aciertos";
		this.respuesta[7] = "Reintegro";
		this.respuesta[8] = "Sin premio";

		try {
			// Crear el socket del servidor
			serverSocket = new ServerSocket(puerto);
			// llama a los metodos generarCombinacion e imprimirCombinacion
			generarCombinacion();
			imprimirCombinacion();

			// esperar la conexión del cliente
			System.out.println("Esperando conexión del cliente...");
			// se acepta al cliente
			cliente = serverSocket.accept();
			System.out.println("Cliente conectado");

			// Inicializar flujo de entrada y salida
			entrada = new BufferedReader(new InputStreamReader(this.cliente.getInputStream()));
			salida = new PrintWriter(this.cliente.getOutputStream(), true);
		} catch (IOException e) {
			System.err.println("Error al iniciar el servidor: " + e.getMessage());
		}
	}

	/**
	 * @return Debe leer la combinacion de numeros que le envia el cliente
	 */
	public String leerCombinacion() {

		try {
			// utilizamos el BufferedReader para leer el mensaje del cliente
			String respuesta = entrada.readLine();
			// comprobamos que la entrada no sea ni null ni FIN
			if (respuesta != null && !respuesta.equals("FIN")) {
				// alamcenamos en un array los numeros elinando los espacios entre ellos
				String numerosAlmacenados[] = respuesta.split(" ");
				// inicializar el array con el numero de elementos recibido por el cliente
				combinacionCliente = new int[numerosAlmacenados.length];
				// bucle for que recorre el array con los numeros y se almacena en
				// combinacionCliente
				for (int i = 0; i < numerosAlmacenados.length; i++) {
					combinacionCliente[i] = Integer.parseInt(numerosAlmacenados[i]);

				}
			}
			return respuesta;
		} catch (IOException e) {
			return null;
		}

	}

	/**
	 * Metodo para comprobar cuantos acierto ha tenido el cliente y si hay numero
	 * respetidos o erroneos
	 * 
	 * @return devuelve el numero de aciertos que ha tenido utilizando el array de
	 *         respuestas
	 */
	public String comprobarBoleto() {
		// if para comprobar que la combinación sea válida
		if (combinacionCliente == null || combinacionCliente.length != 6) {
			return respuesta[8];
		}
		// creacion de variable int para almacenar el numero de aciertos y booleanos
		// para comprobar el reintegro y el complementario
		int aciertos = 0;
		boolean complementarioAcertado = false;
		boolean reintegroAcertado = false;

		// set para guardar los numeros y no se repitan
		Set<Integer> numeros = new HashSet<>();

		for (int numero : combinacionCliente) {
			// comprobar que solo sean numeros validos
			if (numero < 1 || numero > 49) {
				return respuesta[1];
			}
			// comprobar que los numeros no esten repetidos
			if (!numeros.add(numero)) {
				return respuesta[0];
			}
			// bucle for para comprobar cuantos aciertos ha tenido el cliente
			for (int valor : combinacion) {
				if (valor == numero) {
					aciertos++;
				}

			}
			// if para comporbar si se ha acertado el complementario
			if (numero == complementario) {
				complementarioAcertado = true;
			}
			// if para comporbar si se ha acertado el reintegro
			if (numero == reintegro) {
				reintegroAcertado = true;
			}
		}
		// respuesta que se dara segun el numero de aciertos que ha tenido el cliente
		if (aciertos == 6) {
			return respuesta[2];
		} else if (aciertos == 5 && complementarioAcertado) {
			return respuesta[3];
		} else if (aciertos == 5) {
			return respuesta[4];
		} else if (aciertos == 4) {
			return respuesta[5];
		} else if (aciertos == 3) {
			return respuesta[6];
		} else if (reintegroAcertado) {
			return respuesta[7];
		} else {
			return respuesta[8];
		}

	}

	/**
	 * Metodo para enviar respuestas al cliente
	 * 
	 * @param respuesta se debe enviar al ciente, se utilza el PrintWriter
	 */
	public void enviarRespuesta(String respuesta) {
		salida.println(respuesta);
	}

	/**
	 * Cierra el servidor
	 */
	public void finSesion() {
		try {
			// cierra el serverSocket
			serverSocket.close();
			System.out.println(" -> Servidor Finalizado");

		} catch (IOException e) {
			System.err.println("Error al cerrar el servidor: " + e.getMessage());
		}
	}

	/**
	 * Metodo que genera una combinacion. NO MODIFICAR
	 */
	private void generarCombinacion() {
		Set<Integer> numeros = new TreeSet<Integer>();
		Random aleatorio = new Random();
		while (numeros.size() < 6) {
			numeros.add(aleatorio.nextInt(49) + 1);
		}
		int i = 0;
		this.combinacion = new int[6];
		for (Integer elto : numeros) {
			this.combinacion[i++] = elto;
		}
		this.reintegro = aleatorio.nextInt(49) + 1;
		this.complementario = aleatorio.nextInt(49) + 1;
	}

	/**
	 * Metodo que saca por consola del servidor la combinacion
	 */
	private void imprimirCombinacion() {
		System.out.print("Combinaci�n ganadora: ");
		for (Integer elto : this.combinacion)
			System.out.print(elto + " ");
		System.out.println("");
		System.out.println("Complementario:       " + this.complementario);
		System.out.println("Reintegro:            " + this.reintegro);
	}

}
