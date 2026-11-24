package es.upm.pdl.semantico;

import java.util.List;

public final class CompatibilidadTipos {
	
	private CompatibilidadTipos() {}
	
	
	/** Resultado de *.
	 * ENTERO * ENTERO -> ENTERO y REAL * REAL -> REAL
	 * @param izq
	 * @param der
	 * @return
	 */
	public static Atributos multiplicacion(Atributos izq, Atributos der) {
		if(izq.esError() || der.esError()) return Atributos.error();
		
		TipoDato tIzq = izq.getTipoDato();
		TipoDato tDer = der.getTipoDato();
		if(tIzq != null && tIzq == tDer && tIzq.esNumerico())
			return Atributos.simple(tIzq);
		return Atributos.error();
	}
	
	
	/** Resultado de ==.
	 * TIPO == TIPO -> LOGICO, donde TIPO puede ser ENTERO, REAL, LOGICO, CADENA
	 * @param izq
	 * @param der
	 * @return
	 */
	public static Atributos igualdad(Atributos izq, Atributos der) {
		if(izq.esError() || der.esError()) return Atributos.error();
		
		TipoDato tIzq = izq.getTipoDato();
		TipoDato tDer = der.getTipoDato();
		if(tIzq != null && tIzq == tDer && tIzq.esAlmacenable())
			return Atributos.simple(TipoDato.LOGICO);
		return Atributos.error();
	}
	
	
	/** Resultado de operador logico &&.
	 * LOGICO && LOGICO -> LOGICO
	 * @param izq
	 * @param der
	 * @return
	 */
	public static Atributos logico(Atributos izq, Atributos der) {
		if(izq.esError() || der.esError()) return Atributos.error();
		
		TipoDato tIzq = izq.getTipoDato();
		TipoDato tDer = der.getTipoDato();
		if(tIzq == TipoDato.LOGICO && tDer == TipoDato.LOGICO)
			return Atributos.simple(TipoDato.LOGICO);
		return Atributos.error();
	}
	
	/*
	 * Compatibilidad en asignacion
	 */
	
	/** Comprueba si origen puede asignarse al destino.
	 * Necesita que destina tenga el mismo tipo que origen
	 * @param destino
	 * @param origen
	 * @return
	 */
	public static boolean compatiblesAsignacion(TipoDato destino, TipoDato origen) {
		if(destino == null || origen == null)	return false;
		if(destino.esError() || origen.esError())	return false;
		if(!destino.esAlmacenable())	return false;
		return destino == origen;
	}
	
	/** Comprueba si origen puede asignarse con *= al destino
	 * Necesita que sea numerico y del mismo tipo que origen
	 * @param destino
	 * @param origen
	 * @return
	 */
	public static boolean compatiblesAsigMult(TipoDato destino, TipoDato origen) {
		return destino != null && destino.esNumerico() && compatiblesAsignacion(destino, origen);
	}
	
	
	/*
	 * Firmas de funcion
	 */
	
	/** Comprueba que los tipos de los argumentos reales coincidan exactamente
	 * con los de los parametros formales
	 * @param formales
	 * @param reales
	 * @return
	 */
	public static boolean firmaCompatible(List<TipoDato> formales, List<TipoDato> reales) {
		if(formales == null || reales == null)	return false;
		if(formales.size() != reales.size())	return false;
		
		for(int i = 0; i < formales.size(); i++) {
			if(!compatiblesAsignacion(formales.get(i), reales.get(i)))
				return false;
		}
		return true;
	}
	
	
	
	
	
	
	
	
	
}
