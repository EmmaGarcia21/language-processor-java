package es.upm.pdl.semantico;

public enum TipoDato {
	ENTERO	(1),
	REAL	(2),
	LOGICO	(1), 
	CADENA	(64),
	VOID	(0),
	
	OK		(0),
	ERROR	(0);
	
	
	public final int tamano;//Palabras que ocupa
	
	TipoDato(int tamano){
		this.tamano = tamano;
	}
	
	/** Convierte el TipoDato a un string que acepte TS_Gestor.
	 * Devuelve null para tipos no almacenables (VOID, ERROR, OK)
	 * @return
	 */
	public String toTSString() {
		return switch (this) {
		case ENTERO -> "entero";
		case REAL -> "real";
		case CADENA -> "cadena";
		case LOGICO -> "lógico";
		default -> null;
		};
	}
	
	/** Convierte el string devuelto por TS_Gestor a TipoDato
	 * @param s
	 * @return
	 */
	public static TipoDato fromTSString(String s) {
		if(s == null) return ERROR;
		return switch(s) {
		case "entero"	-> ENTERO;
		case "real" 	-> REAL;
		case "lógico" 	-> LOGICO;
		case "cadena"	-> CADENA;
		case "función" 	-> null;//funcion se usa en TipoForma
		default -> ERROR;
		};
	}
	
	public boolean esNumerico() {
		return this == ENTERO || this == REAL;
	}
	
	public boolean esAlmacenable() {
		return this.toTSString() != null;
	}
	
	public boolean esError() {
		return this == ERROR;
	}
}
