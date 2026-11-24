package es.upm.pdl.semantico;

import java.util.ArrayList;
import java.util.List;

import es.upm.pdl.gestor_errores.GestorErrores;
import es.upm.pdl.gestor_errores.TipoError;
import es.upm.pdl.lexico.Token;
import es.upm.pdl.tabla_simbolos.TS_Gestor;
import es.upm.pdl.tabla_simbolos.TS_Gestor.Tabla;

public class GestorFunciones {
	//Constantes de atributos
	static final String ATR_DESPL		= "desplazamiento";
	static final String ATR_NUM_PARAM	= "numParametro";
	static final String ATR_TIPO_PARAM 	= "tipoParametro";
	static final String ATR_MODO_PARAM 	= "modoParametro";
	static final String ATR_TIPO_RET 	= "tipoRetorno";
	static final String ATR_ETIQUETA 	= "etiqueta";
	
	static final String PASO_POR_VALOR = "1";
	
	private final TS_Gestor gestorTS;
	private final ContextoSemantico contexto;
	private final GestorErrores gestorErrores;
	
	//Estado temporal durante el procesamiento de la funcion actual
	private int posFuncionActual = 0;
	private List<TipoDato> parametrosActuales = new ArrayList<>();
	private static int contEtiqueta = 0;
	
	public GestorFunciones(TS_Gestor gestorTS, ContextoSemantico contexto, GestorErrores gestorErrores) {
		this.gestorTS = gestorTS;
		this.contexto = contexto;
		this.gestorErrores = gestorErrores;
	}
	
	/**Llamar al reconocer funcion.
	 * Añade la funcion a la tabla global y crea la tabla local
	 * @param idToken
	 * @param retorno
	 * @return
	 */
	public int iniciarFuncion(Token idToken, TipoDato retorno) {
		String lexema = idToken.lexema;
		
		if(gestorTS.getEntradaTSGlobal(lexema) != 0) {
			reportarError(TipoError.YA_DECLARADO, idToken);
			return 0;
		}
		
		int pos = gestorTS.addEntradaTSGlobal(lexema);
		gestorTS.setTipo(pos, "función");
		
		String etiqueta = "Et" + (++contEtiqueta);
		gestorTS.setValorAtributoCad(pos, ATR_ETIQUETA, etiqueta);
		
		String tipoRet = retorno == TipoDato.VOID? "void" : retorno.toTSString();
		gestorTS.setValorAtributoCad(pos, ATR_TIPO_RET, tipoRet);
		
		posFuncionActual = pos;
		parametrosActuales = new ArrayList<>();
		
		gestorTS.createTSLocal();
		contexto.entrarFuncion();
		return pos;
	}
	
	/**LLamar por cada parametro formal reconocido
	 * @param tipo
	 * @param idToken
	 */
	public int registrarParametro(TipoDato tipo, Token idToken) {
		String lexema = idToken.lexema;
		
		if(gestorTS.getEntradaTSLocal(lexema) != 0) {
			reportarError(TipoError.YA_DECLARADO, idToken);
			return 0;
		}
		
		int pos = gestorTS.addEntradaTSLocal(lexema);
		if(pos == 0) return 0;
		
		gestorTS.setTipo(pos, tipo.toTSString());
		int desp = contexto.reservarLocal(tipo.tamano);
		gestorTS.setValorAtributoEnt(pos, ATR_DESPL, desp);
		
		parametrosActuales.add(tipo);
		return pos;
	}
	
	/**LLamar al cerrar la lista de parametros
	 * 
	 */
	public void finalizarParametros() {
		if(posFuncionActual == 0) return;
		
		int n = parametrosActuales.size();
		gestorTS.setValorAtributoEnt(posFuncionActual, ATR_NUM_PARAM, n);
		
		String[] tipos = new String[n];
		String[] modos = new String[n];
		for(int i = 0; i < n; i++) {
			tipos[i] = parametrosActuales.get(i).toTSString();
			modos[i] = PASO_POR_VALOR;	//Siempre se pasa por valor
		}
		
		gestorTS.setValorAtributoLista(posFuncionActual, ATR_TIPO_PARAM, tipos);
		gestorTS.setValorAtributoLista(posFuncionActual, ATR_MODO_PARAM, modos);
	}
	
	/** LLamar al cerrar el cuerpo de la funcion
	 * 
	 */
	public void finalizarFuncion() {
		gestorTS.write(Tabla.LOCAL);
		gestorTS.destroy(Tabla.LOCAL);
		contexto.salirFuncion();
		posFuncionActual = 0;
		parametrosActuales = new ArrayList<>();
	}
	
	
	/** Comprueba si los argumentos reales coinciden con los parametros formales
	 * @param posFuncion
	 * @param argumentos
	 * @return
	 */
	public boolean comprobarFirma(int posFuncion, List<TipoDato> argumentos) {
		if(argumentos == null) return false;
		
		int numEsperado = gestorTS.getValorAtributoEnt(posFuncion, ATR_NUM_PARAM);
		if(numEsperado < 0 || numEsperado != argumentos.size()) return false;
		if(numEsperado == 0) return true;
		
		String[] tiposTS = gestorTS.getValorAtributoLista(posFuncion, ATR_TIPO_PARAM);
		if(tiposTS == null) return false;
		
		List<TipoDato> formales = new ArrayList<>();
		for(String s: tiposTS)
			formales.add(TipoDato.fromTSString(s));
		
		return CompatibilidadTipos.firmaCompatible(formales, argumentos);
	}
	
	
	/** Devuelve el tipo de retorno de una funcion por su posicion en la tabla global
	 * @param posFuncion
	 * @return
	 */
	public TipoDato obtenerTipoRetorno(int posFuncion) {
		String s = gestorTS.getValorAtributoCad(posFuncion, ATR_TIPO_RET);
		if("void".equals(s)) 
			return TipoDato.VOID;
		return TipoDato.fromTSString(s);
	}
	
	public void reportarError(TipoError tipo, Token token) {
		gestorErrores.errorSemantico(tipo, token.nLinea, token.nCol, token.lexema);
	}
	
	
}
