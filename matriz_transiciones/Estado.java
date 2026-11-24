package es.upm.pdl.matriz_transiciones;

/**
 * Estados del automata asociado a la gramatica del Lexico
 */
public enum Estado {
	INICIO,
	
	ENTERO,						//leyendo digitos enteros Ej: 12
	PUNTO_DECIMAL,				//despues del '.', necesita al menos un digito Ej: 2.
	REAL,						//leyendo digitos decimales Ej: 2.3
	
	CADENA_APERTURA,			//despues de '
	CADENA_INTERIOR,			//dentro de la cadena con al menos un caracter Ej: 'a
	
	IDENTIFICADOR,				//leyendo letras digitos _
	
	MULT_PARCIAL,				//leido *, decide * o *=
	IGUAL_PARCIAL,				//leido =, decide = o ==
	AND_PARCIAL,				//leido &, espera &
	BARRA_PARCIAL,				//leido /, espera *
	
	COMENTARIO,					//dentro de /* ... */
	COMENTARIO_CIERRE_PARCIAL,	//dentro de comentario, leido *, espera /
	
	FIN_ENTERO,
	FIN_REAL,
	FIN_CADENA,
	FIN_CADENA_VACIA,
	FIN_IDENTIFICADOR,
	
	FIN_MULT,			// *
	FIN_ASIG_MULT,		// *=
	FIN_ASIGNACION,		// =
	FIN_IGUAL,			// ==
	FIN_AND,			// &&
	
	FIN_PAREN_ABRE,		// (
	FIN_PAREN_CIERRA,	// )
	FIN_LLAVE_ABRE,		// {
	FIN_LLAVE_CIERRA,	// }
	FIN_PUNTO_COMA,		// ;
	FIN_COMA,			// ,
	
	FIN_EOF;
	
	/**
	 * @return Verdadero si se ha reconocido un token completo
	 */
	public boolean esFinal() {
		return name().startsWith("FIN_");
	}
}
