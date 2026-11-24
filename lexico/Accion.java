package es.upm.pdl.lexico;

/**
 * Accion semantica asociada a una transicion
 * Modifica el Contexto lexico
 */
@FunctionalInterface
public interface Accion {
	void ejecutar(ContextoLexico ctx);
}
