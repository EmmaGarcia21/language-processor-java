package es.upm.pdl.lexico;

public class ContextoLexico {
	
	//Caracter actual que se esta procesando
	public int caracter = 0;
	
	//Valor numerico acumulado
	public long valor = 0;
	
	//Numero de digitos decimales leidos despues del punto
	public int exponente = 0;
	
	//Acumulador de lexema para identificadores y cadenas
	public StringBuilder lexema = new StringBuilder();
	
	//Longitud del contenido de la cadena (sin contar las comillas)
	public int longLexema = 0;
	
	public String getLexema() {
		return lexema.toString();
	}
}
