package es.upm.pdl.semantico;


/** Forma estructural de Atributos.
 *
 * SIMPLE: expresion escalar
 * LISTA: lista de tipos acumulados
 * FUNCION: identificador de funcion
 * VACIO: produccion lambda, sentencia sin valor
 */
public enum TipoForma {
	SIMPLE,
	LISTA,
	FUNCION,
	VACIO
}
