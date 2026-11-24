package es.upm.pdl.utilidades;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class LectorFichero implements Closeable{

	private BufferedReader reader;
	private String bufferLinea;
	private int nLinea;
	private int nColumna;

	private int lineaInicioToken;
	private int columnaInicioToken;

	private boolean cargarSiguienteLinea = false;

	public LectorFichero(Reader reader) throws IOException {
		this.reader = new BufferedReader(reader);
		this.nLinea = 1;
		this.nColumna = 0;
		this.lineaInicioToken = 0;
		this.columnaInicioToken = 0;
		this.bufferLinea = this.reader.readLine();
	}
	
	public LectorFichero(Path ruta) throws IOException {
		this(Files.newBufferedReader(ruta));
	}
	
	public static LectorFichero fromString(String contenido) {
		try {
			return new LectorFichero(new StringReader(contenido));
		} catch (IOException e) {
			throw new AssertionError("No deberia ocurrir", e);
		}
	}
	

	public int leer() {
		if(bufferLinea == null) //Fin de fichero
			return -1;
		try {
			if(cargarSiguienteLinea) {
				bufferLinea = reader.readLine();
				nLinea++;
				nColumna = 0;
				cargarSiguienteLinea = false;

				if(bufferLinea == null) 
					return -1;
			}
			if(nColumna == bufferLinea.length()) {
				cargarSiguienteLinea = true;
				return '\n';
			}
			char c = bufferLinea.charAt(nColumna);
			nColumna++;
			return c;
		} catch (IOException e) {
			return -1;
		}
		

	}

	public void marcarInicioToken() {
		this.lineaInicioToken = nLinea;
		this.columnaInicioToken = nColumna;
	}

	public String getLinea() {return bufferLinea;}

	public int getLineaActual()		{return nLinea;}
	public int getColumnaActual()	{return nColumna;}

	public String getLineaActualTexto() {return bufferLinea;}

	public int getLineaInicioToken() 	{return lineaInicioToken;}
	public int getColumnaInicioToken()	{return columnaInicioToken;}
	
	@Override
	public void close() throws IOException {
		if(reader != null) reader.close();
	}

}

