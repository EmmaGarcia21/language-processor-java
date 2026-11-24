package es.upm.pdl.matriz_transiciones;

import java.util.HashMap;
import java.util.Map;

import es.upm.pdl.gestor_errores.TipoError;

/**
 * Tabla de transiciones para UN UNICO ESTADO del automata
 */
public class EstadoTransiciones {

	private final Map<Integer, Transicion> porCaracter = new HashMap<>();
	private final Map<GrupoCaracter, Transicion> porGrupo = new HashMap<>();
	
	private static final Transicion ERROR_DEFECTO = new Transicion(TipoError.CARACTER_INESPERADO);
	
	/**Registra una transicion para un caracter concreto
	 * @param caracter
	 * @param t
	 * @return
	 */
	public EstadoTransiciones add(int caracter, Transicion t) {
		porCaracter.put(caracter, t);
		return this;
	}
	
	/**Registra una transicion para un grupo de caracteres
	 * @param grupo 
	 * @param t
	 * @return
	 */
	public EstadoTransiciones add(GrupoCaracter grupo, Transicion t) {
		porGrupo.put(grupo, t);
		return this;
	}
	

	/**A partir del estado, busca una transicion.
	 * Primero busca por caracter especifico (c), luego por grupo de caracter (grupo) y por ultimo
	 * por el grupo CUALQUIER_OTRO. 
	 * Si no encuentra ninguna, devuelve una transicion de error
	 * @param c
	 * @param grupo
	 * @return
	 */
	public Transicion get(int c, GrupoCaracter grupo) {
		//Primero intentamos buscar si hay una transicion con el caracter especifico
		Transicion t = porCaracter.get(c);
		if (t != null) {return t;}
		
		//Luego si hay una transicion asociada al grupo del caracter
		if(grupo != null) {
			t = porGrupo.get(grupo);
			if (t != null) {return t;}
		}
		
		//Intentamos buscar una transicion si llega CUALQUIER_OTRO caracter
		return porGrupo.getOrDefault(GrupoCaracter.CUALQUIER_OTRO, ERROR_DEFECTO);
	}
	
}
