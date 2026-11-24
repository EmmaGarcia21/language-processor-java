package es.upm.pdl.lexico;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import es.upm.pdl.gestor_errores.GestorErrores;
import es.upm.pdl.gestor_errores.TipoError;
import es.upm.pdl.matriz_transiciones.Estado;
import es.upm.pdl.matriz_transiciones.MatrizTransiciones;
import es.upm.pdl.matriz_transiciones.Transicion;
import es.upm.pdl.sintactico.Sintactico;
import es.upm.pdl.tabla_simbolos.TS_Gestor;
import es.upm.pdl.utilidades.EscritorFichero;
import es.upm.pdl.utilidades.LectorFichero;

public class Lexico {

	private final LectorFichero lectorFichero;
	private final GestorErrores gestorErrores;
	
	
	private final MatrizTransiciones matrizTransiciones;
	private final List<Token> listaTokens;

	//Constantes del lenguaje
	public static final Set<String> PALABRAS_RESERVADAS = Set.of(
			"true", "false", "int", "float", "string", "boolean", "void",
			"let", "if", "do", "while", "function", "return", "write", "read"
			);
	private static final int MAXIMO_ENTERO = 32767;
	private static final int MAXIMO_REAL = 117549436;
	private static final int MAXIMO_CADENA = 64;


	private int caracter;

	public Lexico(LectorFichero lectorFichero, GestorErrores gestorErrores) {
		this.lectorFichero = lectorFichero;
		this.gestorErrores = gestorErrores;
		
		this.listaTokens = new ArrayList<>();
		this.matrizTransiciones = new MatrizTransiciones();
		this.caracter = lectorFichero.leer();
	}

	public Token ALex() {
		ContextoLexico ctx = new ContextoLexico();
		ctx.caracter = this.caracter;
		Estado estado = Estado.INICIO;

		while(!estado.esFinal()) {
			Transicion transicion = matrizTransiciones.getTransicion(estado, ctx.caracter);
			if(transicion.esError()) {
				reportarError(transicion.tipoError, ctx);
				this.caracter = lectorFichero.leer();//Saltamos el caracter erroneo
				return ALex();
			}
			if(estado == Estado.INICIO && transicion.estadoFinal != Estado.INICIO)
				lectorFichero.marcarInicioToken();
			transicion.accion.ejecutar(ctx);
			if(transicion.consumeCaracter) {
				this.caracter = lectorFichero.leer();
				ctx.caracter = this.caracter;
			}
			estado = transicion.estadoFinal;
		}
		Token tk = emitirToken(estado, ctx);
		return tk;
	}


	
	/**Construye el token correspondiente. 
	 * Además detecta errores de las acciones semanticas
	 * @param estado
	 * @param ctx
	 * @return
	 */
	public Token emitirToken(Estado estado, ContextoLexico ctx) {
		return switch(estado) {
		
			case FIN_ENTERO -> {
				if(ctx.valor > MAXIMO_ENTERO) {
					reportarError(TipoError.ENTERO_FUERA_RANGO, ctx);
					yield ALex();
				}
				yield generarToken(TipoToken.CTEENTERA, (int) ctx.valor);
			}
			case FIN_REAL -> {
				float valorReal = ctx.valor / (float) Math.pow(10, ctx.exponente);
				if(valorReal > MAXIMO_REAL) {
					reportarError(TipoError.REAL_FUERA_RANGO, ctx);
					yield ALex();
				}
				yield generarToken(TipoToken.CTEREAL, valorReal);
			}
			case FIN_CADENA_VACIA -> generarToken(TipoToken.CADENA, "");
			case FIN_CADENA -> {
				if(ctx.longLexema > MAXIMO_CADENA) {
					reportarError(TipoError.CADENA_MAS_64, ctx);
					yield ALex();
				}
				yield generarToken(TipoToken.CADENA, ctx.getLexema());
			}
			
			case FIN_IDENTIFICADOR 	-> emitirIdentificador(ctx);

			case FIN_AND			-> generarToken(TipoToken.OPLOGICO, null);
			case FIN_MULT 			-> generarToken(TipoToken.OPARITMETICO, null);
			case FIN_IGUAL 			-> generarToken(TipoToken.OPRELACIONAL, null);
			
			case FIN_ASIG_MULT 		-> generarToken(TipoToken.OPASIGMULT, null);
			case FIN_ASIGNACION 	-> generarToken(TipoToken.OPASIGNACION, null);
			
			case FIN_PAREN_ABRE 	-> generarToken(TipoToken.PARENTESISABRE, null);
			case FIN_PAREN_CIERRA	-> generarToken(TipoToken.PARENTESISCIERRA, null);
			case FIN_LLAVE_ABRE 	-> generarToken(TipoToken.LLAVEABRE, null);
			case FIN_LLAVE_CIERRA 	-> generarToken(TipoToken.LLAVECIERRA, null);
			case FIN_PUNTO_COMA 	-> generarToken(TipoToken.PUNTOCOMA, null);
			case FIN_COMA 			-> generarToken(TipoToken.COMA, null);
			
			case FIN_EOF -> generarToken(TipoToken.EOF, null);
			
			default -> null;
		};
	}
	
	
	/** Construye un token ID o un token de palabra reservada
	 * @param ctx
	 * @return
	 */
	public Token emitirIdentificador(ContextoLexico ctx) {
		String lexema = ctx.getLexema();
		if(PALABRAS_RESERVADAS.contains(lexema)) {
			return switch (lexema) {
			case "true" 	->	generarToken(TipoToken.TRUE, null);
			case "false" 	-> generarToken(TipoToken.FALSE, null);
			case "int" 		-> generarToken(TipoToken.INT, null);
			case "float" 	-> generarToken(TipoToken.FLOAT, null);
			case "string" 	-> generarToken(TipoToken.STRING, null);
			case "boolean" 	-> generarToken(TipoToken.BOOLEAN, null);
			case "void" 	-> generarToken(TipoToken.VOID, null);
			case "let" 		-> generarToken(TipoToken.LET, null);
			case "if" 		-> generarToken(TipoToken.IF, null);
			case "do" 		-> generarToken(TipoToken.DO, null);
			case "while" 	-> generarToken(TipoToken.WHILE, null);
			case "function"	-> generarToken(TipoToken.FUNCTION, null);
			case "return" 	-> generarToken(TipoToken.RETURN, null);
			case "write" 	-> generarToken(TipoToken.WRITE, null);
			case "read" 	-> generarToken(TipoToken.READ, null);
			default -> null; //nunca deberia ocurrir
			};
		}
				
		Token tk = generarToken(TipoToken.ID, 0);//posTS la actualiza el semantico usando la referencia del token
		tk.lexema = lexema;	//Para depuracion
		return tk;
	}
	
	/** Construye un Token con la posicion marcada al inicio del lexema y lo añade
	 * a la lista de tokens para el fichero de salida
	 * @param tipo
	 * @param atributo
	 * @return
	 */
	public Token generarToken(TipoToken tipo, Object atributo) {
		int nLinea = lectorFichero.getLineaInicioToken();
		int nCol = lectorFichero.getColumnaInicioToken();
		
		Token token;
		if(tipo == TipoToken.CTEENTERA || tipo == TipoToken.ID)
			token = new Token(tipo, nLinea, nCol, ((Number) atributo).intValue());
		else if(tipo == TipoToken.CTEREAL)
			token = new Token(tipo, nLinea, nCol, ((Number) atributo).floatValue());
		else if(tipo == TipoToken.CADENA)
			token = new Token(tipo, nLinea, nCol, (String) atributo);
		else
			token = new Token(tipo, nLinea, nCol);
		
		listaTokens.add(token);
		return token;
	} 
	
	
	/** Devuelve la lista de Tokens generados
	 * @return
	 */
	public List<Token> getTokens() {
		return new ArrayList<>(listaTokens);
	}
	
	/** Escribe la lista de tokens en el fichero indicado
	 * Fromato: un token por linea, usando Token.toString()
	 * 
	 * @param ruta
	 */
	public void escribirTokens(Path ruta) {
		EscritorFichero.escribir(ruta, listaTokens);
	}
	
	public void reportarError(TipoError tipo, ContextoLexico ctx) {
		gestorErrores.errorLexico(tipo,
				lectorFichero.getLineaInicioToken(), lectorFichero.getColumnaInicioToken(),
				lectorFichero.getLineaActual(), lectorFichero.getColumnaActual(),
				ctx.caracter);
	}

}
