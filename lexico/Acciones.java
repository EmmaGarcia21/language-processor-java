package es.upm.pdl.lexico;

/**
 * Conjunto de acciones semanticas predefinidas
 */
public class Acciones {
	
	public static final Accion SIN_ACCION	= ctx -> {};
	
	public static final Accion INICIAR_ENTERO = ctx -> {
		ctx.valor = ctx.caracter - '0';
	};
	public static final Accion ACUMULAR_ENTERO = ctx -> {
		ctx.valor = ctx.valor * 10 + (ctx.caracter - '0');
	};
	public static final Accion INICIAR_DECIMAL = ctx -> {
		ctx.exponente = 0;
	};
	public static final Accion ACUMULAR_REAL = ctx -> {
		ctx.valor = ctx.valor * 10 + (ctx.caracter - '0');
		ctx.exponente++;
	};
	public static final Accion INICIAR_CADENA = ctx -> {
		ctx.lexema = new StringBuilder();
		ctx.longLexema = 0;
	};
	public static final Accion ACUMULAR_CADENA = ctx -> {
		ctx.lexema.append((char) ctx.caracter);
		ctx.longLexema++;
	};
	public static final Accion INICIAR_ID = ctx -> {
		ctx.lexema = new StringBuilder();
		ctx.lexema.append((char) ctx.caracter);
	};
	public static final Accion ACUMULAR_ID = ctx -> {
		ctx.lexema.append((char) ctx.caracter);
	};
}
