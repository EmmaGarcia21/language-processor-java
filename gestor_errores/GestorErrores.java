package es.upm.pdl.gestor_errores;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import es.upm.pdl.utilidades.EscritorFichero;

public class GestorErrores {

	private final boolean modoVerboso;
	private final int maxErrores;

	private final List<ErrorCompilacion> listaErrores;
	private boolean compilacionAbortada;

	private int erroresLexicos;
	private int erroresSintacticos;
	private int erroresSemanticos;

	public GestorErrores(boolean modoVerboso, int maxErrores) {
		this.modoVerboso = modoVerboso;
		this.maxErrores = maxErrores;
		
		this.listaErrores = new ArrayList<>();
		this.compilacionAbortada = false;
		
		this.erroresLexicos = 0;
		this.erroresSintacticos = 0;
		this.erroresSemanticos = 0;
	}

	public void errorLexico(TipoError tipo, int lineaToken, int colToken,
			int lineaDeteccion, int colDeteccion, int caracter) {
		registrar(
				ErrorCompilacion.lexico(tipo, lineaToken, colToken, lineaDeteccion, colDeteccion, caracter)
				);
		erroresLexicos++;
	}

	public void errorSintactico(TipoError tipo, int linea, int col, String lexema, String tipoToken) {
		registrar(
				ErrorCompilacion.sintactico(tipo, linea, col, lexema, tipoToken)
				);
		erroresSintacticos++;
	}

	public void errorSintacticoEsperado(int linea, int col, String lexema, 
			String tipoRecibido, String tipoEsperado) {
		registrar(
				ErrorCompilacion.sintacticoEsperado(linea, col, lexema, tipoRecibido, tipoEsperado)
				);
		erroresSintacticos++;
	}

	public void errorSemantico(TipoError tipo, int linea, int col, String lexema) {
		registrar(
				ErrorCompilacion.semantico(tipo, linea, col, lexema, lexema)
				);
		erroresSemanticos++;
	}

	public void errorSemantico(TipoError tipo, int linea, int col, String lexema,
			String infoAdicional) {
		registrar(
				ErrorCompilacion.semantico(tipo, linea, col, lexema, infoAdicional)
				);
		erroresSemanticos++;
	}

	
	public void registrar(ErrorCompilacion error) {
		listaErrores.add(error);

		if(modoVerboso)
			System.err.println(error);

		if(maxErrores > 0 && 
				(erroresLexicos + erroresSintacticos + erroresSemanticos) >= maxErrores) {
			compilacionAbortada = true;
		}
	}
	
	public List<ErrorCompilacion> getErrores(){
		return new ArrayList<>(listaErrores);
	}
	
	public boolean compilacionAbortada() {
		return compilacionAbortada;
	}
	
	public boolean hayErrores() {
		return !listaErrores.isEmpty();
	}
	
	public boolean hayError(TipoError tipo) {
		for(ErrorCompilacion error: listaErrores) {
			if(error.tipo == tipo)
				return true;
		}
		return false;
	}
	
	public boolean hayError(CategoriaError categoria) {
		for(ErrorCompilacion error: listaErrores) {
			if(error.categoria == categoria)
				return true;
		}
		return false;
	}
	
	public int numErrores(TipoError tipo) {
		int cont = 0;
		for(ErrorCompilacion error: listaErrores) {
			if(error.tipo == tipo)
				cont++;
		}
		return cont;
	}
	
	public void escribirErrores(Path ruta) {
		if(listaErrores.isEmpty()) return;
		EscritorFichero.escribir(ruta, listaErrores);
	}
	
	public void imprimirResumen() {
		if(!hayErrores()) {
			System.out.println("Compilacion sin errores");
			return;
		}
		
		System.err.println("\n======== Resumen de errores ============");
		System.err.println("Lexicos: " + erroresLexicos);
		System.err.println("Sintacticos: " + erroresSintacticos);
		System.err.println("Semanticos: " + erroresSemanticos);
		
	}
}
