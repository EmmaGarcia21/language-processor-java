package es.upm.pdl.semantico;

import java.util.List;

import es.upm.pdl.gestor_errores.GestorErrores;
import es.upm.pdl.gestor_errores.TipoError;
import es.upm.pdl.lexico.Lexico;
import es.upm.pdl.lexico.Token;
import es.upm.pdl.tabla_simbolos.TS_Gestor;
import es.upm.pdl.tabla_simbolos.TS_Gestor.DescripcionAtributo;
import es.upm.pdl.tabla_simbolos.TS_Gestor.Tabla;
import es.upm.pdl.tabla_simbolos.TS_Gestor.TipoDatoAtributo;

public class Semantico {
	
	
	
	private final TS_Gestor gestorTS;
	private final GestorErrores gestorErrores;
	
	private final ContextoSemantico contexto;
	private final GestorDeclaracionesImplicitas implicitas;
	private final GestorFunciones funciones;
	
	public Semantico(TS_Gestor gestorTS, GestorErrores gestorErrores) {
		this.gestorTS = gestorTS;
		this.gestorErrores = gestorErrores;
		this.contexto = new ContextoSemantico();
		this.implicitas = new GestorDeclaracionesImplicitas(gestorTS, contexto);
		this.funciones = new GestorFunciones(gestorTS, contexto, gestorErrores);
		
		inicializarTS();
	}
	
	/**Inicializa la Tabla de Simbolos, creando la tabla de palabras reservadas,
	 *  la tabla global y configurando los campos que almacena
	 */
	private void inicializarTS() {
		gestorTS.createTPalabrasReservadas();
		for(String palabraReservada: Lexico.PALABRAS_RESERVADAS) {
			gestorTS.addEntradaTPalabrasReservadas(palabraReservada);
		}
		
		gestorTS.createAtributo(GestorFunciones.ATR_DESPL, DescripcionAtributo.DIR, TipoDatoAtributo.ENTERO);
		gestorTS.createAtributo(GestorFunciones.ATR_NUM_PARAM, DescripcionAtributo.NUM_PARAM, TipoDatoAtributo.ENTERO);
		gestorTS.createAtributo(GestorFunciones.ATR_TIPO_PARAM, DescripcionAtributo.TIPO_PARAM, TipoDatoAtributo.LISTA);
		gestorTS.createAtributo(GestorFunciones.ATR_MODO_PARAM, DescripcionAtributo.MODO_PARAM, TipoDatoAtributo.LISTA);
		gestorTS.createAtributo(GestorFunciones.ATR_TIPO_RET, DescripcionAtributo.TIPO_RET, TipoDatoAtributo.CADENA);
		gestorTS.createAtributo(GestorFunciones.ATR_ETIQUETA, DescripcionAtributo.ETIQUETA, TipoDatoAtributo.CADENA);
		gestorTS.createTSGlobal();
	}
	
	/**Declara una variable.
	 * Añade a la tabla activa, asigna el tipo y desplazamiento
	 * @param idToken
	 * @param tipo
	 * @return
	 */
	public int declararVariable(Token idToken, TipoDato tipo) {
		String lexema = idToken.lexema;
		int posExistente;
		if(contexto.enFuncion())
			posExistente = gestorTS.getEntradaTSLocal(lexema);
		else
			posExistente = gestorTS.getEntradaTSGlobal(lexema);
		
		if(posExistente != 0 && !implicitas.esPendiente(posExistente)) {
			gestorErrores.errorSemantico(TipoError.YA_DECLARADO, idToken.nLinea, idToken.nCol, lexema);
			return 0;
		}
		
		int pos;
		if(posExistente != 0) {
			//Estaba como implicita, rellenamos el tipo ahora
			pos = posExistente;
		}
		else {
			if(contexto.enFuncion())
				pos = gestorTS.addEntradaTSLocal(lexema);
			else
				pos = gestorTS.addEntradaTSGlobal(lexema);
		}
		
		int res = gestorTS.setTipo(pos, tipo.toTSString());
		if(res == 7) {
			gestorErrores.errorSemantico(TipoError.YA_DECLARADO, idToken.nLinea, idToken.nCol, lexema);
			return 0;
		}
		
		int desp;
		if(contexto.enFuncion())
			desp = contexto.reservarLocal(tipo.tamano);
		else
			desp = contexto.reservarGlobal(tipo.tamano);
		gestorTS.setValorAtributoEnt(pos, GestorFunciones.ATR_DESPL, desp);
		
		return desp;
	}
	
	/**Gestiona el uso de un identificador en una expresion.
	 * Si no esta declarado, lo registra como declaracion implicita global.
	 * @param idToken
	 * @return
	 */
	public Atributos registrarUsoIdentificador(Token idToken) {
		String lexema = idToken.lexema;
		int pos = gestorTS.getEntradaTS(lexema);
		
		if(pos == 0) {
			System.out.println("[AVISO] La variable " + lexema + " no ha sido declarada");
			pos = implicitas.registrar(idToken);
			Atributos atr = Atributos.simple(TipoDato.ENTERO);
			atr.posTS = implicitas.getDesplazamiento(pos);
			return atr;
		}
		
		//Si esta pendiente de materializacion, sabemos que es ENTERO implicito
		if(implicitas.esPendiente(pos)) {
			Atributos atr = Atributos.simple(TipoDato.ENTERO);
			atr.posTS = implicitas.getDesplazamiento(pos);
			return atr;
		}
		
		String tipoStr = gestorTS.getTipo(pos);
		Atributos atr;
		if("función".equals(tipoStr)) {
			atr = Atributos.funcion(funciones.obtenerTipoRetorno(pos));
			atr.posTS = pos;//posTS, para comprobar firma
		}
		else {
			TipoDato tipo = TipoDato.fromTSString(tipoStr);
			if(tipo == null) {
				atr = Atributos.error();
				atr.posTS = pos;
			}
			else {
				atr = Atributos.simple(tipo);
				atr.posTS = gestorTS.getValorAtributoEnt(pos, GestorFunciones.ATR_DESPL);
			}
		}
		return atr;
	}
	
	/**Devuelve el tipo almacenado en la TS para la posicion dada
	 * @param pos
	 * @return
	 */
	public TipoDato obtenerTipo(int pos) {
		String s = gestorTS.getTipo(pos);
		if("función".equals(s)) return null;
		return TipoDato.fromTSString(s);
	}
	
	
	/*METODOS FACHADA*/
	
	public int abrirFuncion(Token idToken, TipoDato retorno) {
		return funciones.iniciarFuncion(idToken, retorno);
	}
	
	public int registrarParametro(TipoDato tipo, Token idToken) {
		return funciones.registrarParametro(tipo, idToken);
	}
	
	public void finalizarParametros() {
		funciones.finalizarParametros();
	}
	
	public void cerrarFuncion() {
		funciones.finalizarFuncion();
	}
	
	public boolean comprobarFirma(int posFuncion, List<TipoDato> args) {
		return funciones.comprobarFirma(posFuncion, args);
	}
	
	public void finalizar() {
		implicitas.materializar();
		gestorTS.write(Tabla.GLOBAL);
		gestorTS.destroy(Tabla.GLOBAL);
		gestorTS.destroy(Tabla.PALRES);
	}
	
	
	
}
