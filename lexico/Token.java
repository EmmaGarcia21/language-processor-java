package es.upm.pdl.lexico;

public class Token {
	public TipoToken tipo;
	
	public int valorInt;
	public float valorFloat;
	public String lexema;
	public int posTS;
	
	public int nLinea;
	public int nCol;
	
	public Token(TipoToken tipo, int nLinea, int nCol) {
		this.tipo = tipo;
		this.nLinea = nLinea;
		this.nCol = nCol;
	}
	
	public Token(TipoToken tipo, int nLinea, int nCol, int valor) {
		this(tipo, nLinea, nCol);

		if(this.tipo == TipoToken.ID)//<ID, punteroTablaSimbolos>
			this.posTS = valor;
		else//<CTEENTERA, valor>
			this.valorInt = valor;
	}
	
	public Token(TipoToken tipo, int nLinea, int nCol, float valorFloat) {
		this(tipo, nLinea, nCol);
		this.valorFloat = valorFloat;
	}
	
	public Token(TipoToken tipo, int nLinea, int nCol, String lexema) {
		this(tipo, nLinea, nCol);
		this.lexema = lexema;
	}
	
	
	
	/** Representacion a String del token. Se usa para construir el fichero de token
	 * @return Representacion del token acorde a su tipo y parametros
	 */
	public String toString() {
		if(tipo == TipoToken.CTEENTERA)
			return "<" + tipo + ", " + valorInt + ">";
		else if(tipo == TipoToken.CTEREAL)
			return "<" + tipo + ", " + valorFloat + ">";
		else if(tipo == TipoToken.CADENA)
			return "<" + tipo + ", \"" + lexema + "\">";
		else if (tipo == TipoToken.ID)
			return "<" + tipo + ", " + posTS + ">";
		else 	
			return "<" + tipo + ", >";
	}
	
}
