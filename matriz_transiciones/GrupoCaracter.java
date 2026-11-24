package es.upm.pdl.matriz_transiciones;

/**
 * Grupo de caracteres. Usado para las transiciones del automata del Lexico
 */
public enum GrupoCaracter {
	//Espacios, tabuladores
	DELIMITADOR,
	
	//Del 0 al 9
	DIGITO,
	
	//Letra o guion bajo
	LETRA_O_GUION_BAJO,
	
	//Letra, digito o guion bajo
	ALFANUMERICO_O_GUION_BAJO,
	
	//Cualquier imprimible excepto '
	IMPRIMIBLE_SIN_COMILLA_SIMPLE,
	
	//Cualquier imprimible excepto *
	IMPRIMIBLE_SIN_ASTERISCO,
	
	//Cualquier imprimible excepto / y *
	IMPRIMIBLE_SIN_BARRA_ASTERISCO,
	
	SIGUIENTE_A_DIGITO,
	SIGUIENTE_A_IDENTIFICADOR,
	SIGUIENTE_A_OP_BINARIO,
	SIGUIENTE_A_IGUAL,
	
	// Fin de fichero (-1 del lector)
	EOF_CHAR,
	
	//Ningun grupo anterior aplico. "eoc"
	CUALQUIER_OTRO
}
