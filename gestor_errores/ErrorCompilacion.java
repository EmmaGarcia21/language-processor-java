package es.upm.pdl.gestor_errores;

public class ErrorCompilacion {
	
	public final CategoriaError categoria;
	public final TipoError tipo;
	
	public final int linea;
	public final int columna;
	public final String mensaje;
	
	//Campos del lexico
	public final int lineaDeteccion;
	public final int colDeteccion;
	public final int caracter;
	
	//Campos del sintactico, semantico
	public final String lexema;
	public final String tipoTokenRecibido;
	public final String tipoTokenEsperado;
	
	public ErrorCompilacion(CategoriaError categoria, TipoError tipo, int linea, int columna, String mensaje,
			int lineaDeteccion, int colDeteccion, int caracter, String lexema, String tipoTokenRecibido,
			String tipoTokenEsperado) {
		super();
		this.categoria = categoria;
		this.tipo = tipo;
		this.linea = linea;
		this.columna = columna;
		this.mensaje = mensaje;
		this.lineaDeteccion = lineaDeteccion;
		this.colDeteccion = colDeteccion;
		this.caracter = caracter;
		this.lexema = lexema;
		this.tipoTokenRecibido = tipoTokenRecibido;
		this.tipoTokenEsperado = tipoTokenEsperado;
	}
	
	/*CONSTRUCTORES*/
	public static ErrorCompilacion lexico (TipoError tipo, int lineaToken, int colToken,
			int lineaDeteccion, int colDeteccion, int caracter) {
		String msg = construirMensajeLexico(tipo, lineaToken, colToken, lineaDeteccion, colDeteccion, caracter);
		String caracterString = caracter > 0 ? String.valueOf((char) caracter) : "";
		
		return new ErrorCompilacion(CategoriaError.LEXICO, tipo,
				lineaToken, colToken, msg,
				lineaDeteccion, colDeteccion, caracter,
				caracterString, null, null);
	}
	
	public static ErrorCompilacion sintactico(TipoError tipo, int linea, int columna, 
			String lexema, String tipoToken) {
		String msg = "[%s] %s: '%s' (%s)"
			.formatted(tipo.getMensajeBase(), ubicacion(linea, columna), lexema, tipoToken);
		return new ErrorCompilacion(CategoriaError.SINTACTICO, tipo,
				linea, columna, msg,
				-1, -1, -1,
				lexema, tipoToken, null);
	}
	
	public static ErrorCompilacion sintacticoEsperado(int linea, int columna, String lexema, 
			String tipoRecibido, String tipoEsperado) {
		String msg = "Se esperaba '%s' pero se encontro '%s' (%s) en %s"
			.formatted(tipoEsperado, tipoRecibido, lexema, ubicacion(linea, columna));
		return new ErrorCompilacion(CategoriaError.SINTACTICO, TipoError.TIPO_TOKEN_INESPERADO,
				linea, columna, msg,
				-1, -1, -1,
				lexema, tipoRecibido, tipoEsperado);
	}
	
	public static ErrorCompilacion semantico(TipoError tipo, int linea, int columna,
			String lexema, String infoAdicional) {
		String msg = construirMensajeSemantico(tipo, linea, columna, lexema, infoAdicional);
		return new ErrorCompilacion(CategoriaError.SEMANTICO, tipo,
				linea, columna, msg,
				-1, -1, -1,
				lexema, null, null);
	}
	
	/*Construccion de mensajes*/
	
	private static String construirMensajeLexico(TipoError tipo, int lineaToken, int colToken,
			int lineaDeteccion, int colDeteccion, int caracter) {
		return switch(tipo) {
		case CARACTER_INESPERADO ->
			"Caracter inseperado '%c' (U+%04X) en %s"
			.formatted((char) caracter, caracter, ubicacion(lineaDeteccion, colDeteccion));
		
		case SALTO_LINEA_EN_CADENA ->
			"Cadena sin cerrar: apertura en %s, salto de linea en %s"
			.formatted(ubicacion(lineaToken, colToken), ubicacion(lineaDeteccion, colDeteccion));
		case EOF_EN_CADENA_ABIERTA ->
			"Cadena sin cerrar: apertura en %s, fin de fichero en linea %d"
			.formatted(ubicacion(lineaToken, colToken), lineaDeteccion);
		case COMENTARIO_NO_CERRADO ->
			"Comentario sin cerrar (abierto en %s), fin de fichero en linea %d"
			.formatted(ubicacion(lineaToken, colToken), lineaDeteccion);
		case ENTERO_FUERA_RANGO -> 
			"Constante entera fuera de rango en %s"
			.formatted(ubicacion(lineaToken, colToken));
		case REAL_FUERA_RANGO ->
			"Constante real fuera de rango en %s"
			.formatted(ubicacion(lineaToken, colToken));
		case CADENA_MAS_64 ->
			"Cadena con mas de 64 caracteres (inicio en %s)"
			.formatted(ubicacion(lineaToken, colToken));
		case BARRA_SIN_ASTERISCO ->
			"'/' sin '*' a continuacion en %s. Querias escribir /* */?"
			.formatted(ubicacion(lineaDeteccion, colDeteccion));
		case OPERADOR_AND_INCOMPLETO ->
			"Solo un '&' en %s (se esperaba '&&')"
			.formatted(ubicacion(lineaDeteccion, colDeteccion));
		case PUNTO_SIN_DECIMALES ->
			"Punto decimal sin digitos en %s"
			.formatted(ubicacion(lineaDeteccion, colDeteccion));
		default ->
			"%s en %s"
			.formatted(tipo.getMensajeBase(), ubicacion(lineaDeteccion, colDeteccion));
		};
	}
	
	private static String construirMensajeSemantico(TipoError tipo, int linea, int col,
			String lexema, String infoAdicional) {
		String ubi = ubicacion(linea, col);
		return switch(tipo) {
		case YA_DECLARADO ->
			"El identificador '%s' ya esta declarado este ambito (%s)"
			.formatted(lexema, ubi);
		case TIPOS_INCOMPATIBLES -> {
			if(infoAdicional != null)
				yield "Tipos incompatibles en %s: %s".formatted(ubi, infoAdicional);
			else
				yield "Tipos incompatibles en %s".formatted(ubi);
		}
		case FIRMA_INCORRECTA -> 
			"La llamada a '%s' no coincide con su declaracion (%s)"
			.formatted(lexema, ubi);
		case TIPO_RETORNO_INCORRECTO -> {
			if(infoAdicional != null)
				yield "Tipo de retorno incorrecto en %s: %s".formatted(ubi, infoAdicional);
			else
				yield "Tipo de retorno incorrecto en %s".formatted(ubi);
		}
		case LLAMADA_A_NO_FUNCION ->
			"'%s' no es una funcion (%s)".formatted(lexema, ubi);
			
		default -> "%s: '%s' en %s".formatted(tipo.getMensajeBase(), lexema, ubi);
		};
	}
	
	private static String ubicacion(int linea, int col) {
		return "linea %d, col %d".formatted(linea, col);
	}
	
	@Override
	public String toString() {
		return "[%s] %s".formatted(categoria, mensaje);
	}

}
