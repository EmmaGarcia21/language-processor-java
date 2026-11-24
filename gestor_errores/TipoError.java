package es.upm.pdl.gestor_errores;

public enum TipoError {

	/*LEXICOS*/
	
	//Errores detectados por el Lexico durante la tokenizacion
	CARACTER_INESPERADO		(CategoriaError.LEXICO),
	EOF_INESPERADO			(CategoriaError.LEXICO),
	BARRA_SIN_ASTERISCO		(CategoriaError.LEXICO),
	OPERADOR_AND_INCOMPLETO	(CategoriaError.LEXICO),
	COMENTARIO_NO_CERRADO	(CategoriaError.LEXICO),
	SALTO_LINEA_EN_CADENA	(CategoriaError.LEXICO),
	EOF_EN_CADENA_ABIERTA	(CategoriaError.LEXICO),
	PUNTO_SIN_DECIMALES		(CategoriaError.LEXICO),
	
	//Errores de rango/tamano
	ENTERO_FUERA_RANGO		(CategoriaError.LEXICO),
	REAL_FUERA_RANGO		(CategoriaError.LEXICO),
	CADENA_MAS_64			(CategoriaError.LEXICO),
	
	//Error interno del automata
	ESTADO_INVALIDO 		(CategoriaError.LEXICO),
	
	/*SINTACTICOS*/
	TIPO_TOKEN_INESPERADO	(CategoriaError.SINTACTICO),
	TOKEN_INESPERADO		(CategoriaError.SINTACTICO),
	FINAL_INCORRECTO		(CategoriaError.SINTACTICO),
	
	/*SEMANTICOS*/
	YA_DECLARADO 			(CategoriaError.SEMANTICO),
	TIPOS_INCOMPATIBLES 	(CategoriaError.SEMANTICO),
	FIRMA_INCORRECTA 		(CategoriaError.SEMANTICO),
	TIPO_RETORNO_INCORRECTO (CategoriaError.SEMANTICO),
	
	LLAMADA_A_NO_FUNCION	(CategoriaError.SEMANTICO),
	
	/*GENERICOS*/
	ERROR_DEFECTO 			(CategoriaError.OTRO);
	
	private final CategoriaError categoria;
	
	private TipoError(CategoriaError categoria) {
		this.categoria = categoria;
	}
	
	public CategoriaError getCategoria() {
		return categoria;
	}
	
	public String getMensajeBase() {
		return switch (this) {
		case CARACTER_INESPERADO 		-> "Caracter inesperado";
		case EOF_INESPERADO 			-> "Fin de fichero inesperado";
		case BARRA_SIN_ASTERISCO 		-> "'/' debe ir seguido de '*' para abrir comentario";
		case OPERADOR_AND_INCOMPLETO	-> "'&' debe ir seguido de '&'";
		case COMENTARIO_NO_CERRADO		-> "Comentario de bloque sin cerrar";
		case SALTO_LINEA_EN_CADENA		-> "Salto de linea dentro de una cadena";
		case EOF_EN_CADENA_ABIERTA 		-> "Fin de fichero dentro de una cadena sin cerrar";
		case PUNTO_SIN_DECIMALES		-> "Se esperaba al menos un digito tras el punto decimal";
		
		case ENTERO_FUERA_RANGO 		-> "Constante entera fuera de rango";
		case REAL_FUERA_RANGO			-> "Constante real fuera de rango";
		case CADENA_MAS_64				-> "Cadena con mas de 64 caracteres";
		case ESTADO_INVALIDO			-> "Error interno del analizador lexico";
		
		case TIPO_TOKEN_INESPERADO		-> "Token inesperado";
		case TOKEN_INESPERADO			-> "Token invalido en este contexto";
		case FINAL_INCORRECTO			-> "Fin de programa incorrecto";
		
		case YA_DECLARADO				-> "Identificador ya declarado";
		case TIPOS_INCOMPATIBLES		-> "Tipos incompatibles";
		case FIRMA_INCORRECTA			-> "La firma de la llamada no corresponde con la declaracion";
		
		case TIPO_RETORNO_INCORRECTO	-> "Tipo de retorno incorrecto";
		case LLAMADA_A_NO_FUNCION		-> "El identificador no es una funcion";
		case ERROR_DEFECTO				-> "Error de compilacion";
		};
	}
	
}
