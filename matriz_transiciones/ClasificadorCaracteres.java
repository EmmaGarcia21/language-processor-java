package es.upm.pdl.matriz_transiciones;

public class ClasificadorCaracteres {
	
	public static GrupoCaracter clasificar(Estado estado, int c) {
		if(c == -1)	return GrupoCaracter.EOF_CHAR;
		
		return switch(estado) {
		
			case INICIO -> {
				if(esDelimitador(c)) 		yield GrupoCaracter.DELIMITADOR;
				if(esDigito(c))				yield GrupoCaracter.DIGITO;
				if(esLetraOGuionBajo(c)) 	yield GrupoCaracter.LETRA_O_GUION_BAJO;
				yield GrupoCaracter.CUALQUIER_OTRO;
			}
			
			case ENTERO, REAL -> {
				if(esDigito(c))				yield GrupoCaracter.DIGITO;
				if(esSiguienteADigito(c)) 	yield GrupoCaracter.SIGUIENTE_A_DIGITO;
				yield GrupoCaracter.CUALQUIER_OTRO;
			}
			
			case PUNTO_DECIMAL -> {
				if(esDigito(c)) yield GrupoCaracter.DIGITO;
				yield GrupoCaracter.CUALQUIER_OTRO;
			}
		
			case CADENA_APERTURA, CADENA_INTERIOR -> {
				if(esImprimibleSinComillaSimple(c)) yield GrupoCaracter.IMPRIMIBLE_SIN_COMILLA_SIMPLE;
				yield GrupoCaracter.CUALQUIER_OTRO;
			}
			
			case IDENTIFICADOR -> {
				if(esSiguienteAIdentificador(c)) yield GrupoCaracter.SIGUIENTE_A_IDENTIFICADOR;
				if(esAlfanumericoOGuionBajo(c))	 yield GrupoCaracter.ALFANUMERICO_O_GUION_BAJO;
				yield GrupoCaracter.CUALQUIER_OTRO;
			}
			
			case MULT_PARCIAL -> {
				if(esSiguienteAOperadorBinario(c)) yield GrupoCaracter.SIGUIENTE_A_OP_BINARIO;
				yield GrupoCaracter.CUALQUIER_OTRO;
			}
			
			case IGUAL_PARCIAL -> {
				if(esSiguienteAIgual(c)) yield GrupoCaracter.SIGUIENTE_A_IGUAL;
				yield GrupoCaracter.CUALQUIER_OTRO;
			}
			
			case COMENTARIO -> {
				if(esImprimibleSinAsterisco(c)) yield GrupoCaracter.IMPRIMIBLE_SIN_ASTERISCO;
				yield GrupoCaracter.CUALQUIER_OTRO;
			}
			
			case COMENTARIO_CIERRE_PARCIAL -> {
				if(esImprimibleSinBarraYAsterisco(c)) yield GrupoCaracter.IMPRIMIBLE_SIN_BARRA_ASTERISCO;
				yield GrupoCaracter.CUALQUIER_OTRO;
			}
			
			//Estados sin grupo solo tiene entrada con caracteres concretos
			default -> GrupoCaracter.CUALQUIER_OTRO;
		};
		
	}
	
	private static boolean esDelimitador(int c) {
		return Character.isWhitespace(c);
	}
	
	private static boolean esDigito(int c) {
		return Character.isDigit(c);
	}
	
	private static boolean esLetraOGuionBajo(int c) {
		return c == '_' || Character.isLetter(c);
	}
	
	private static boolean esAlfanumericoOGuionBajo(int c) {
		return esLetraOGuionBajo(c) || esDigito(c);
	}
	
	private static boolean esImprimible(int c) {
		return esDelimitador(c) || !Character.isISOControl(c);
	}
	
	private static boolean esImprimibleSinComillaSimple(int c) {
		return c != '\'' && c != '\n' && esImprimible(c);
	}
	
	private static boolean esImprimibleSinAsterisco(int c) {
		return c != '*' && esImprimible(c);
	}
	
	private static boolean esImprimibleSinBarraYAsterisco(int c) {
		return c != '/' && c != '*' && esImprimible(c);
	}
	
	private static boolean esSiguienteADigito (int c) {
		return 	esDelimitador(c)
				|| c == '*' || c == '&' || c == '=' 
				|| c == ';' || c == ',' || c == '/'
				|| c == ')' || c == '}';
	}
	
	public static boolean esSiguienteAIdentificador (int c) {
		return 	esDelimitador(c)
				|| c == '*' || c == '&' || c == '=' 
				|| c == ';' || c == ',' || c == '\''|| c == '/'
				|| c == '(' || c == ')' || c == '{' || c == '}';
	}
	
	public static boolean esSiguienteAOperadorBinario (int c) {
		return	esDelimitador(c) || esAlfanumericoOGuionBajo(c) || c == '\'' || c == '(';
	}
	
	public static boolean esSiguienteAIgual (int c) {
		return 	esDelimitador(c) || esAlfanumericoOGuionBajo(c)
				|| c == '(' || c == '\'';
	}
	
}
