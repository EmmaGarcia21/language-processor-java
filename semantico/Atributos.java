package es.upm.pdl.semantico;

import java.util.ArrayList;
import java.util.List;

public class Atributos {
	private final TipoDato tipoDato;	//Tipo real del dato
	private final TipoForma forma;		//Estructura: si es simple, lista, etc
	private final List<TipoDato> listaTipos;	//Solo si forma == LISTA
	private final TipoDato tipoRet;

	//Posicion en TS del identificador
	public int posTS = 0;


	public Atributos(TipoDato tipoDato, TipoForma forma, List<TipoDato> listaTipos, TipoDato tipoRet) {
		this.tipoDato = tipoDato;
		this.forma = forma;
		this.listaTipos = listaTipos;
		this.tipoRet = tipoRet;
	}

	/*CONSTRUCTORES*/

	/** Error semantico. Se propaga a traves del arbol
	 * @return
	 */
	public static Atributos error() {
		return new Atributos(TipoDato.ERROR, TipoForma.SIMPLE, null, null);
	}

	/** Produccion lambda o retorno void
	 * @return
	 */
	public static Atributos vacio() {
		return new Atributos(TipoDato.VOID, TipoForma.VACIO, null, null);
	}

	/** Sentencia ejecutada correctamente, sin tener un valor con tipo
	 * @return
	 */
	public static Atributos ok() {
		return new Atributos(TipoDato.OK, TipoForma.VACIO, null, null);
	}

	/** Expresion escalar con un tipo en concreto
	 * @param tipo
	 * @return
	 */
	public static Atributos simple(TipoDato tipo) {
		if(tipo == null || tipo.esError()) {
			return error();
		}
		return new Atributos(tipo, TipoForma.SIMPLE, null, null);
	}

	/** Lista de tipos de argumentos, para verificar la firma de una llamada
	 * @param tipos
	 * @return
	 */
	public static Atributos lista(List<TipoDato> tipos) {
		return new Atributos(null, TipoForma.LISTA, new ArrayList<>(tipos), null);
	}

	/** Identificador de funcion
	 * @param tipoRetorno
	 * @return
	 */
	public static Atributos funcion(TipoDato tipoRetorno) {
		return new Atributos(null, TipoForma.FUNCION, null, tipoRetorno);
	}

	/*GETTERS*/
	public TipoDato getTipoDato()			{return tipoDato;}
	public TipoForma getForma() 			{return forma;}
	public List<TipoDato> getListaTipos()	{return listaTipos;}
	public TipoDato getTipoRet() 			{return tipoRet;}

	/*CONSULTAS*/
	public boolean esOk()		{return tipoDato == TipoDato.OK;}
	public boolean esError() 	{return tipoDato == TipoDato.ERROR;}
	public boolean esVacio() 	{return forma == TipoForma.VACIO && tipoDato == TipoDato.VOID;}
	public boolean esSimple() 	{return forma == TipoForma.SIMPLE;}
	public boolean esFuncion()	{return forma == TipoForma.FUNCION;}
	public boolean esLista() 	{return forma == TipoForma.LISTA;}

}
