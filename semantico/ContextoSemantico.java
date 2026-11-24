package es.upm.pdl.semantico;

import es.upm.pdl.tabla_simbolos.TS_Gestor.Tabla;

public class ContextoSemantico {
	
	private Tabla tablaActual;
	private int despGlobal;
	private int despLocal;
	
	public ContextoSemantico() {
		this.tablaActual = Tabla.GLOBAL;
		this.despGlobal = 1;
		this.despLocal = -1;
	}
	
	/*
	 * Reserva de memoria
	 */
	
	public int reservarGlobal(int tam) {
		int desp = despGlobal;
		despGlobal += tam;
		return desp;
	}
	
	public int reservarLocal(int tam) {
		int desp = despLocal;
		despLocal -= tam;
		return desp;
	}
	
	/*
	 * Transicion de ambito: LOCAL <==> GLOBAL
	 */
	
	public void entrarFuncion() {
		tablaActual = Tabla.LOCAL;
		despLocal = -1;
	}
	
	public void salirFuncion() {
		tablaActual = Tabla.GLOBAL;
	}
	
	public boolean enFuncion() {return tablaActual == Tabla.LOCAL;}
	
	/*
	 * Getters
	 */
	public Tabla getTablaActual() {return tablaActual;}
	public int getDespGlobal() {return despGlobal;}
	public int getDespLocal() {return despLocal;}
	
	
}
