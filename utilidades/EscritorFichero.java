package es.upm.pdl.utilidades;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class EscritorFichero {
	
	
	/** Escribe cada elemento de la lista en una linea del fichero
	 * Usa toString() para la representacion
	 * @param <T>
	 * @param ruta
	 * @param elementos
	 */
	public static <T> void escribir(Path ruta, List<T> elementos) {
		try {
			Files.createDirectories(ruta.getParent());
			
			try(PrintWriter printWriter = new PrintWriter(Files.newBufferedWriter(
					ruta,
					StandardOpenOption.CREATE,
					StandardOpenOption.TRUNCATE_EXISTING,
					StandardOpenOption.WRITE))){
				for(T elemento: elementos) {
					printWriter.println(elemento);
				}
			}
		} catch (IOException e) {
			System.err.println("Error al escribir el fichero: " + ruta + "\n" + e.getMessage());
		}
	}
	
	/** Escribe cada elemento de la lista en una linea del fichero
	 * Usa toString() para la representacion
	 * @param <T>
	 * @param ruta
	 * @param elementos
	 */
	public static <T> void escribir(Path ruta, String titulo, List<T> elementos) {
		try {
			Files.createDirectories(ruta.getParent());
			
			try(PrintWriter printWriter = new PrintWriter(Files.newBufferedWriter(
					ruta,
					StandardOpenOption.CREATE,
					StandardOpenOption.TRUNCATE_EXISTING,
					StandardOpenOption.WRITE))){
				printWriter.println(titulo);
				for(T elemento: elementos) {
					printWriter.println(elemento);
				}
			}
		} catch (IOException e) {
			System.err.println("Error al escribir el fichero: " + ruta + "\n" + e.getMessage());
		}
	}
	
	
	
}
