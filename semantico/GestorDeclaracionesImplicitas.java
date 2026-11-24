package es.upm.pdl.semantico;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import es.upm.pdl.lexico.Token;
import es.upm.pdl.tabla_simbolos.TS_Gestor;

public class GestorDeclaracionesImplicitas {
	
	private final TS_Gestor gestorTS;
	private final ContextoSemantico contexto;
	
	//<pos, desplazamiento>, desplazamiento reservado por orden de aparicion
	private final Map<Integer, Integer> desplazamientos;
	
	public GestorDeclaracionesImplicitas(TS_Gestor gestorTS, ContextoSemantico ctx) {
		this.gestorTS = gestorTS;
		this.contexto = ctx;
		this.desplazamientos = new LinkedHashMap<>();
	}

	/**Registra un identificador no declarado, lo hace como declaracion implicita.
	 * Se considera entera y global.
	 * @param idToken
	 * @return
	 */
	public int registrar(Token idToken) {
		String lexema = idToken.lexema;
		
		//Reutilizamos si ya fue registrada como implicita antes
		int pos = gestorTS.getEntradaTSGlobal(lexema);
		if(pos != 0) {
			//Si ya esta pendiente de materializar, devolvemos la misma posicion
			if(desplazamientos.containsKey(pos))
				return pos;
			//Si ya tiene tipo (declaracion explicita), la posicion es valida
			return pos;
		}
		
		//Primera aparicion, añadimos a entrada globl
		pos = gestorTS.addEntradaTSGlobal(lexema);
		if(pos == 0) return 0;
		
		//Reservamos desplazamiento global
		int desp = contexto.reservarGlobal(TipoDato.ENTERO.tamano);
		desplazamientos.put(pos, desp);
		
		return pos;
	}
	
	
	/** Asigna tipo "entero" y desplazamiento a todas las entradas implicitas
	 * LLamar al finalizar el analisis del programa, antes de escribir la tabla
	 */
	public void materializar() {
		for(Map.Entry<Integer, Integer> entry: desplazamientos.entrySet()) {
			int pos = entry.getKey();
			int desp = entry.getValue();
			
			gestorTS.setTipo(pos, "entero");
			gestorTS.setValorAtributoEnt(pos, GestorFunciones.ATR_DESPL, desp);
		}
	}
	
	public int getDesplazamiento(int pos) {
		Integer desp = desplazamientos.get(pos);
		return desp != null? desp: 0;
	}
	
	public boolean esPendiente(int pos)	{return desplazamientos.containsKey(pos);}
	public List<Integer> getPendientes() {return new ArrayList<>(desplazamientos.keySet());}
}
