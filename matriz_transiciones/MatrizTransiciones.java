package es.upm.pdl.matriz_transiciones;

import java.util.HashMap;
import java.util.Map;

import es.upm.pdl.gestor_errores.TipoError;
import es.upm.pdl.lexico.Accion;
import es.upm.pdl.lexico.Acciones;

public class MatrizTransiciones {

	private static boolean CONSUME_CHAR = true;
	private static boolean NO_CONSUME_CHAR = false;

	private final Map<Estado, EstadoTransiciones> matriz = new HashMap<>();

	public MatrizTransiciones() {
		construirMatriz();
	}
	
	private Transicion tr(Estado desde, Estado hasta, Accion accion, boolean consume) {
		return new Transicion(desde, hasta, accion, consume);
	}

	private Transicion err(TipoError tipo) {
		return new Transicion(tipo);
	}

	private void construirMatriz() {
		//INICIO
		EstadoTransiciones inicio = new EstadoTransiciones()
				.add(GrupoCaracter.DELIMITADOR, 		tr(Estado.INICIO, Estado.INICIO, Acciones.SIN_ACCION, CONSUME_CHAR))
				.add(GrupoCaracter.DIGITO, 				tr(Estado.INICIO, Estado.ENTERO, Acciones.INICIAR_ENTERO, CONSUME_CHAR))
				.add(GrupoCaracter.LETRA_O_GUION_BAJO,	tr(Estado.INICIO, Estado.IDENTIFICADOR, Acciones.INICIAR_ID, CONSUME_CHAR))
				.add((int) '\'',	tr(Estado.INICIO, Estado.CADENA_APERTURA,	Acciones.INICIAR_CADENA, CONSUME_CHAR))
				.add((int) '*',		tr(Estado.INICIO, Estado.MULT_PARCIAL, 		Acciones.SIN_ACCION, CONSUME_CHAR))
				.add((int) '=', 	tr(Estado.INICIO, Estado.IGUAL_PARCIAL, 	Acciones.SIN_ACCION, CONSUME_CHAR))
				.add((int) '&', 	tr(Estado.INICIO, Estado.AND_PARCIAL, 		Acciones.SIN_ACCION, CONSUME_CHAR))
				.add((int) '/', 	tr(Estado.INICIO, Estado.BARRA_PARCIAL, 	Acciones.SIN_ACCION, CONSUME_CHAR))
				.add((int) '(', 	tr(Estado.INICIO, Estado.FIN_PAREN_ABRE, 	Acciones.SIN_ACCION, CONSUME_CHAR))
				.add((int) ')', 	tr(Estado.INICIO, Estado.FIN_PAREN_CIERRA, 	Acciones.SIN_ACCION, CONSUME_CHAR))
				.add((int) '{', 	tr(Estado.INICIO, Estado.FIN_LLAVE_ABRE, 	Acciones.SIN_ACCION, CONSUME_CHAR))
				.add((int) '}', 	tr(Estado.INICIO, Estado.FIN_LLAVE_CIERRA, 	Acciones.SIN_ACCION, CONSUME_CHAR))
				.add((int) ';', 	tr(Estado.INICIO, Estado.FIN_PUNTO_COMA, 	Acciones.SIN_ACCION, CONSUME_CHAR))
				.add((int) ',', 	tr(Estado.INICIO, Estado.FIN_COMA, 			Acciones.SIN_ACCION, CONSUME_CHAR))
				.add(GrupoCaracter.EOF_CHAR, tr(Estado.INICIO, Estado.FIN_EOF, 	Acciones.SIN_ACCION, NO_CONSUME_CHAR))
				.add(GrupoCaracter.CUALQUIER_OTRO, err(TipoError.CARACTER_INESPERADO));

		//ENTERO
		EstadoTransiciones entero = new EstadoTransiciones()
				.add(GrupoCaracter.DIGITO, tr(Estado.ENTERO, Estado.ENTERO, Acciones.ACUMULAR_ENTERO, CONSUME_CHAR))
				.add(GrupoCaracter.SIGUIENTE_A_DIGITO, tr(Estado.ENTERO, Estado.FIN_ENTERO, Acciones.SIN_ACCION, NO_CONSUME_CHAR))
				.add((int)'.', tr(Estado.ENTERO, Estado.PUNTO_DECIMAL, Acciones.INICIAR_DECIMAL, CONSUME_CHAR))
				//EOF termina el entero como si fuera un separador
				.add(GrupoCaracter.EOF_CHAR, tr(Estado.ENTERO, Estado.FIN_ENTERO, Acciones.SIN_ACCION, NO_CONSUME_CHAR))
				.add(GrupoCaracter.CUALQUIER_OTRO, err(TipoError.CARACTER_INESPERADO));
				

		//PUNTO_DECIMAL
		EstadoTransiciones puntoDecimal = new EstadoTransiciones()
				.add(GrupoCaracter.DIGITO, tr(Estado.PUNTO_DECIMAL, Estado.REAL, Acciones.ACUMULAR_REAL, CONSUME_CHAR))
				.add(GrupoCaracter.EOF_CHAR, err(TipoError.PUNTO_SIN_DECIMALES))
				.add(GrupoCaracter.CUALQUIER_OTRO, err(TipoError.PUNTO_SIN_DECIMALES));
		
		//REAL 
		EstadoTransiciones real = new EstadoTransiciones()
				.add(GrupoCaracter.DIGITO, tr(Estado.REAL, Estado.REAL, Acciones.ACUMULAR_REAL, CONSUME_CHAR))
				.add(GrupoCaracter.SIGUIENTE_A_DIGITO, tr(Estado.REAL, Estado.FIN_REAL, Acciones.SIN_ACCION, NO_CONSUME_CHAR))
				.add(GrupoCaracter.EOF_CHAR, tr(Estado.REAL, Estado.FIN_REAL, Acciones.SIN_ACCION, NO_CONSUME_CHAR))
				.add(GrupoCaracter.CUALQUIER_OTRO, err(TipoError.CARACTER_INESPERADO));
		
		//CADENA_APERTURA 
		EstadoTransiciones cadenaApertura = new EstadoTransiciones()
				.add((int)'\'', tr(Estado.CADENA_APERTURA, Estado.FIN_CADENA_VACIA, Acciones.SIN_ACCION, CONSUME_CHAR))
				.add((int)'\n', err(TipoError.SALTO_LINEA_EN_CADENA))
				.add(GrupoCaracter.EOF_CHAR, err(TipoError.EOF_EN_CADENA_ABIERTA))
				.add(GrupoCaracter.IMPRIMIBLE_SIN_COMILLA_SIMPLE, 
						tr(Estado.CADENA_APERTURA, Estado.CADENA_INTERIOR, Acciones.ACUMULAR_CADENA, CONSUME_CHAR));

		//CADENA_INTERIOR 
		EstadoTransiciones cadenaInterior = new EstadoTransiciones()
				.add((int)'\'', tr(Estado.CADENA_INTERIOR, Estado.FIN_CADENA, Acciones.SIN_ACCION, CONSUME_CHAR))
				.add((int)'\n', err(TipoError.SALTO_LINEA_EN_CADENA))
				.add(GrupoCaracter.EOF_CHAR, err(TipoError.EOF_EN_CADENA_ABIERTA))
				.add(GrupoCaracter.IMPRIMIBLE_SIN_COMILLA_SIMPLE, 
						tr(Estado.CADENA_INTERIOR, Estado.CADENA_INTERIOR, Acciones.ACUMULAR_CADENA, CONSUME_CHAR));

		//IDENTIFICADOR
		EstadoTransiciones identificador = new EstadoTransiciones()
				.add(GrupoCaracter.ALFANUMERICO_O_GUION_BAJO, 
						tr(Estado.IDENTIFICADOR, Estado.IDENTIFICADOR, Acciones.ACUMULAR_ID, CONSUME_CHAR))
				.add(GrupoCaracter.SIGUIENTE_A_IDENTIFICADOR,
						tr(Estado.IDENTIFICADOR, Estado.FIN_IDENTIFICADOR, Acciones.SIN_ACCION, NO_CONSUME_CHAR))
				.add(GrupoCaracter.EOF_CHAR,
						tr(Estado.IDENTIFICADOR, Estado.FIN_IDENTIFICADOR, Acciones.SIN_ACCION, NO_CONSUME_CHAR))
				.add(GrupoCaracter.CUALQUIER_OTRO, err(TipoError.CARACTER_INESPERADO));

		//MULT_PARCIAL
		EstadoTransiciones multParcial = new EstadoTransiciones()
				.add((int)'=', tr(Estado.MULT_PARCIAL, Estado.FIN_ASIG_MULT, Acciones.SIN_ACCION, CONSUME_CHAR))
				.add(GrupoCaracter.SIGUIENTE_A_OP_BINARIO,
						tr(Estado.MULT_PARCIAL, Estado.FIN_MULT, Acciones.SIN_ACCION, NO_CONSUME_CHAR))
				.add(GrupoCaracter.EOF_CHAR,
						tr(Estado.MULT_PARCIAL, Estado.FIN_MULT, Acciones.SIN_ACCION, NO_CONSUME_CHAR))
				.add(GrupoCaracter.CUALQUIER_OTRO, err(TipoError.CARACTER_INESPERADO));

		//IGUAL_PARCIAL
		EstadoTransiciones igualParcial = new EstadoTransiciones()
				.add((int)'=', tr(Estado.IGUAL_PARCIAL, Estado.FIN_IGUAL, Acciones.SIN_ACCION, CONSUME_CHAR))
				.add(GrupoCaracter.SIGUIENTE_A_IGUAL, 
						tr(Estado.IGUAL_PARCIAL, Estado.FIN_ASIGNACION, Acciones.SIN_ACCION, NO_CONSUME_CHAR))
				.add(GrupoCaracter.EOF_CHAR,
						tr(Estado.IGUAL_PARCIAL, Estado.FIN_ASIGNACION, Acciones.SIN_ACCION, NO_CONSUME_CHAR))
				.add(GrupoCaracter.CUALQUIER_OTRO, err(TipoError.CARACTER_INESPERADO));

		//AND_PARCIAL
		EstadoTransiciones andParcial = new EstadoTransiciones()
				.add((int)'&', tr(Estado.AND_PARCIAL, Estado.FIN_AND, Acciones.SIN_ACCION, CONSUME_CHAR))
				.add(GrupoCaracter.CUALQUIER_OTRO, err(TipoError.OPERADOR_AND_INCOMPLETO));

		//BARRA_PARCIAL
		EstadoTransiciones barraParcial = new EstadoTransiciones()
				.add((int)'*', tr(Estado.BARRA_PARCIAL, Estado.COMENTARIO, Acciones.SIN_ACCION, CONSUME_CHAR))
				.add(GrupoCaracter.CUALQUIER_OTRO, err(TipoError.BARRA_SIN_ASTERISCO));

		//COMENTARIO
		EstadoTransiciones comentario = new EstadoTransiciones()
				.add((int) '*', tr(Estado.COMENTARIO, Estado.COMENTARIO_CIERRE_PARCIAL, Acciones.SIN_ACCION, CONSUME_CHAR))
				.add(GrupoCaracter.IMPRIMIBLE_SIN_ASTERISCO, 
						tr(Estado.COMENTARIO, Estado.COMENTARIO, Acciones.SIN_ACCION, CONSUME_CHAR))
				.add(GrupoCaracter.EOF_CHAR, err(TipoError.COMENTARIO_NO_CERRADO));

		//COMENTARIO_CIERRE_PARCIAL
		EstadoTransiciones comentarioCierreParcial = new EstadoTransiciones()
				.add((int) '*', tr(Estado.COMENTARIO_CIERRE_PARCIAL, Estado.COMENTARIO_CIERRE_PARCIAL, Acciones.SIN_ACCION, CONSUME_CHAR))
				.add((int) '/', tr(Estado.COMENTARIO_CIERRE_PARCIAL, Estado.INICIO, Acciones.SIN_ACCION, CONSUME_CHAR))
				.add(GrupoCaracter.IMPRIMIBLE_SIN_BARRA_ASTERISCO,
						tr(Estado.COMENTARIO_CIERRE_PARCIAL, Estado.COMENTARIO, Acciones.SIN_ACCION, CONSUME_CHAR))
				.add(GrupoCaracter.EOF_CHAR, err(TipoError.COMENTARIO_NO_CERRADO));
		
		//Registrar todos los estados
		matriz.put(Estado.INICIO, 			inicio);
		matriz.put(Estado.ENTERO, 			entero);
		matriz.put(Estado.PUNTO_DECIMAL, 	puntoDecimal);
		matriz.put(Estado.REAL, 			real);
		matriz.put(Estado.CADENA_APERTURA, 	cadenaApertura);
		matriz.put(Estado.CADENA_INTERIOR, 	cadenaInterior);
		matriz.put(Estado.IDENTIFICADOR, 	identificador);
		matriz.put(Estado.MULT_PARCIAL, 	multParcial);
		matriz.put(Estado.IGUAL_PARCIAL, 	igualParcial);
		matriz.put(Estado.AND_PARCIAL, 		andParcial);
		matriz.put(Estado.BARRA_PARCIAL, 	barraParcial);
		matriz.put(Estado.COMENTARIO, 		comentario);
		matriz.put(Estado.COMENTARIO_CIERRE_PARCIAL, comentarioCierreParcial);
		//Los estados FIN_ no tienen entradas, pues no tienen transiciones asociadas
	}
	
	public Transicion getTransicion(Estado estado, int c) {
		EstadoTransiciones transiciones = matriz.get(estado);
		if(transiciones == null)
			return new Transicion(TipoError.ESTADO_INVALIDO);
		GrupoCaracter grupo = ClasificadorCaracteres.clasificar(estado, c);
		return transiciones.get(c, grupo);
	}

	

}
