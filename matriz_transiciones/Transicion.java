package es.upm.pdl.matriz_transiciones;

import es.upm.pdl.gestor_errores.TipoError;
import es.upm.pdl.lexico.Accion;

/**
 * Representa una transicion en el automata del lexico. Puede ser:
 * Normal: tiene estado inicial, final, accion y si consume caracter
 * Error: solo tiene tipoError
 */
public class Transicion {
	
	
	public final Estado estadoInicial;
	public final Estado estadoFinal;
	public final Accion accion;
	
	//Indica si se debe leer el siguiente caracter del fichero despues de ejecutar la accion
	public final boolean consumeCaracter;
	
	//Nulo, excepto si hay un error
	public final TipoError tipoError;
	
	/**Transicion normal
	 * @param estadoInicial
	 * @param estadoFinal
	 * @param accion
	 * @param consumeCaracter
	 */
	public Transicion (Estado estadoInicial, Estado estadoFinal,
					Accion accion, boolean consumeCaracter) {
		this.estadoInicial = estadoInicial;
		this.estadoFinal = estadoFinal;
		this.accion = accion;
		this.consumeCaracter = consumeCaracter;
		this.tipoError = null;
	}
	
	/**Transicion de error
	 * @param tipoError
	 */
	public Transicion(TipoError tipoError) {
		this.estadoInicial = null;
		this.estadoFinal = null;
		this.accion = null;
		this.consumeCaracter = false;
		this.tipoError = tipoError;
	}
	
	public boolean esError() {
		return tipoError != null;
	}
}
