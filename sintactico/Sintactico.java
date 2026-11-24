package es.upm.pdl.sintactico;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import es.upm.pdl.gestor_errores.GestorErrores;
import es.upm.pdl.gestor_errores.TipoError;
import es.upm.pdl.lexico.Lexico;
import es.upm.pdl.lexico.TipoToken;
import es.upm.pdl.lexico.Token;
import es.upm.pdl.semantico.Atributos;
import es.upm.pdl.semantico.CompatibilidadTipos;
import es.upm.pdl.semantico.Semantico;
import es.upm.pdl.semantico.TipoDato;
import es.upm.pdl.semantico.TipoForma;
import es.upm.pdl.tabla_simbolos.TS_Gestor;
import es.upm.pdl.utilidades.EscritorFichero;

public class Sintactico {

	private final GestorErrores gestorErrores;

	private final Lexico lexico;
	private final Semantico semantico;

	private final List<ReglaGramatica> listaParse;

	private Token tokenActual;
	
	private Token ultimoOpToken;
	private int ultimaLineaError = -1;
	private int ultimaColError = -1;

	public Sintactico(Lexico lexico, TS_Gestor gestorTS, GestorErrores gestorErrores) {
		this.lexico = lexico;
		this.gestorErrores = gestorErrores;
		this.semantico = new Semantico(gestorTS, gestorErrores);
		this.listaParse = new ArrayList<>();

		this.tokenActual = lexico.ALex();
	}


	public void ASint() {
		P();
		if(tokenActual.tipo != TipoToken.EOF) {
			reportarFinalIncorrecto();
			
		}
		semantico.finalizar();
	}

	private void equipara(TipoToken esperado) {
		if(tokenActual.tipo == esperado) {
			tokenActual = lexico.ALex();
		}
		else {
			reportarTokenEsperado(esperado);
			tokenActual = lexico.ALex();//Saltamos el token de error
		}
	}

	private Atributos P() {
		if(Gramatica.esFirstB(tokenActual.tipo)) {//P -> BP
			registrarRegla(ReglaGramatica.P_BP);
			Atributos atrB = B();
			Atributos atrP2 = P();
			if(atrB.esOk() && atrP2.esOk())
				return Atributos.ok();
			return Atributos.error();
		}
		else if(tokenActual.tipo == TipoToken.FUNCTION) {//P -> FP, First(F) = function
			registrarRegla(ReglaGramatica.P_FP);
			Atributos atrF = F();
			Atributos atrP2 = P();
			if(atrF.esOk() && atrP2.esOk())
				return Atributos.ok();
			return Atributos.error();
		}
		else if(tokenActual.tipo == TipoToken.EOF) {//P -> lambda, Follow(P) = EOF
			registrarRegla(ReglaGramatica.P_LAMBDA);
			return Atributos.ok();
		}
		else {
			reportarTokenInesperado();
			return Atributos.error();
		}
	}

	private Atributos E() {// E -> U E'
		registrarRegla(ReglaGramatica.E_UEPRIMA);
		Atributos atrU = U();
		Atributos atrEPrima = EPrima();
		
		if(atrEPrima.esVacio())
			return atrU;
		if(atrU.esError() || atrEPrima.esError())
			return Atributos.error();
		if(atrU.getTipoDato() == TipoDato.LOGICO && atrEPrima.getTipoDato() == TipoDato.LOGICO)
			return Atributos.simple(TipoDato.LOGICO);

		reportarErrorSemantico(TipoError.TIPOS_INCOMPATIBLES, ultimoOpToken);
		return Atributos.error();
	}

	private Atributos EPrima() {
		if(tokenActual.tipo == TipoToken.OPLOGICO) {//E' -> && U E'
			registrarRegla(ReglaGramatica.EPRIMA_AND);
			Token opToken = tokenActual;
			ultimoOpToken = opToken;
			equipara(TipoToken.OPLOGICO);
			Atributos atrU = U();
			Atributos atrEPrima2 = EPrima();
			
			if(atrU.esError() || atrEPrima2.esError())
				return Atributos.error();
			
			if(atrEPrima2.esVacio()){
				if(atrU.getTipoDato() == TipoDato.LOGICO)
					return Atributos.simple(TipoDato.LOGICO);
				reportarErrorSemantico(TipoError.TIPOS_INCOMPATIBLES, opToken);
				return Atributos.error();
			}
			
			if(atrU.getTipoDato() == TipoDato.LOGICO && atrEPrima2.getTipoDato() == TipoDato.LOGICO)
				return Atributos.simple(TipoDato.LOGICO);
			reportarErrorSemantico(TipoError.TIPOS_INCOMPATIBLES, opToken);
			return Atributos.error();
		}
		else if(Gramatica.esFollowE(tokenActual.tipo)) {//E' -> lambda
			registrarRegla(ReglaGramatica.EPRIMA_LAMBDA);
			return Atributos.vacio();
		}
		else {
			reportarTokenInesperado();
			return Atributos.error();
		}
	}

	private Atributos U() {//U -> V U'
		registrarRegla(ReglaGramatica.U_VUPRIMA);
		Atributos atrV = V();
		Atributos atrUPrima = UPrima();
		
		if(atrUPrima.esVacio())
			return atrV;
		if(atrV.esError() || atrUPrima.esError())
			return Atributos.error();
		if(atrV.getTipoDato() == atrUPrima.getTipoDato())
			return Atributos.simple(TipoDato.LOGICO);
		
		reportarErrorSemantico(TipoError.TIPOS_INCOMPATIBLES, ultimoOpToken);
		return Atributos.error();
	}

	private Atributos UPrima() {
		if(tokenActual.tipo == TipoToken.OPRELACIONAL) {//U' -> == V U'
			registrarRegla(ReglaGramatica.UPRIMA_IGUAL);
			Token opToken = tokenActual;
			ultimoOpToken = opToken;
			equipara(TipoToken.OPRELACIONAL);
			Atributos atrV = V();
			Atributos atrUPrima2 = UPrima();
			
			if(atrV.esError() || atrUPrima2.esError())
				return Atributos.error();
			
			if(atrUPrima2.esVacio())
				return atrV;
			if(atrV.getTipoDato() == atrUPrima2.getTipoDato())
				return Atributos.simple(TipoDato.LOGICO);
			
			reportarErrorSemantico(TipoError.TIPOS_INCOMPATIBLES, opToken);
			return Atributos.error();	
		}
		else if(Gramatica.esFollowU(tokenActual.tipo)) {//U' -> lambda
			registrarRegla(ReglaGramatica.UPRIMA_LAMBDA);
			return Atributos.vacio();
		}
		else {
			reportarTokenInesperado();
			return Atributos.error();
		}
	}

	private Atributos V() {//V	-> Y V'
		registrarRegla(ReglaGramatica.V_YVPRIMA);
		Atributos atrY = Y();
		Atributos atrVPrima = VPrima();
		
		if(atrVPrima.esVacio())
			return atrY;
		if(atrY.esError() || atrVPrima.esError())
			return Atributos.error();
		if(atrY.getTipoDato() == TipoDato.ENTERO && atrVPrima.getTipoDato() == TipoDato.ENTERO)
			return Atributos.simple(TipoDato.ENTERO);
		if(atrY.getTipoDato() == TipoDato.REAL && atrVPrima.getTipoDato() == TipoDato.REAL)
			return Atributos.simple(TipoDato.REAL);
		
		reportarErrorSemantico(TipoError.TIPOS_INCOMPATIBLES, ultimoOpToken);
		return Atributos.error();
	}

	private Atributos VPrima() {
		if(tokenActual.tipo == TipoToken.OPARITMETICO) {//V' -> * Y VPrima
			registrarRegla(ReglaGramatica.VPRIMA_MULT);
			Token opToken = tokenActual;
			ultimoOpToken = opToken;
			equipara(TipoToken.OPARITMETICO);
			Atributos atrY = Y();
			Atributos atrVPrima2 = VPrima();
			
			if(atrY.esError() || atrVPrima2.esError())
				return Atributos.error();
			
			if(atrVPrima2.esVacio()) {
				if(atrY.getTipoDato() == TipoDato.ENTERO || atrY.getTipoDato() == TipoDato.REAL)
					return atrY;
				reportarErrorSemantico(TipoError.TIPOS_INCOMPATIBLES, opToken);
				return Atributos.error();
			}
			
			if(atrY.getTipoDato() == TipoDato.ENTERO && atrVPrima2.getTipoDato() == TipoDato.ENTERO)
				return Atributos.simple(TipoDato.ENTERO);
			if(atrY.getTipoDato() == TipoDato.REAL && atrVPrima2.getTipoDato() == TipoDato.REAL)
				return Atributos.simple(TipoDato.REAL);
			
			reportarErrorSemantico(TipoError.TIPOS_INCOMPATIBLES, opToken);
			return Atributos.error();
		}
		else if(Gramatica.esFollowV(tokenActual.tipo)) {//V' -> lambda
			registrarRegla(ReglaGramatica.VPRIMA_LAMBDA);
			return Atributos.vacio();
		}
		else {
			reportarTokenInesperado();
			return Atributos.error();
		}
	}

	private Atributos Y() {
		if(tokenActual.tipo == TipoToken.PARENTESISABRE) {//Y -> ( E )
			registrarRegla(ReglaGramatica.Y_PAREN);
			equipara(TipoToken.PARENTESISABRE);
			Atributos atrE = E();
			equipara(TipoToken.PARENTESISCIERRA);
			return atrE;
		}
		else if(tokenActual.tipo == TipoToken.CTEENTERA) {//Y -> cteentera
			registrarRegla(ReglaGramatica.Y_ENTERA);
			equipara(TipoToken.CTEENTERA);
			return Atributos.simple(TipoDato.ENTERO);
		}
		else if(tokenActual.tipo == TipoToken.CTEREAL) {//Y -> ctereal
			registrarRegla(ReglaGramatica.Y_REAL);
			equipara(TipoToken.CTEREAL);
			return Atributos.simple(TipoDato.REAL);
		}
		else if(tokenActual.tipo == TipoToken.CADENA) {//Y -> cadena
			registrarRegla(ReglaGramatica.Y_CADENA);
			equipara(TipoToken.CADENA);
			return Atributos.simple(TipoDato.CADENA);
		}
		else if(tokenActual.tipo == TipoToken.TRUE) {//Y -> true
			registrarRegla(ReglaGramatica.Y_TRUE);
			equipara(TipoToken.TRUE);
			return Atributos.simple(TipoDato.LOGICO);
		}
		else if(tokenActual.tipo == TipoToken.FALSE) {//Y -> false
			registrarRegla(ReglaGramatica.Y_FALSE);
			equipara(TipoToken.FALSE);
			return Atributos.simple(TipoDato.LOGICO);
		}
		else if(tokenActual.tipo == TipoToken.ID) {//Y -> id Y'
			registrarRegla(ReglaGramatica.Y_ID);
			Token idToken = tokenActual;
			equipara(TipoToken.ID);
			Atributos atrId = semantico.registrarUsoIdentificador(idToken);
			idToken.posTS = atrId.posTS;
			Atributos atrYPrima = YPrima(atrId, idToken);
			
			if(atrYPrima.esVacio())
				return atrId;
			return atrYPrima;
		}
		else {
			reportarTokenInesperado();
			return Atributos.error();
		}
	}

	private Atributos YPrima(Atributos atrId, Token idToken) {
		if(tokenActual.tipo == TipoToken.PARENTESISABRE) {//Y' -> ( L )
			registrarRegla(ReglaGramatica.YPRIMA_LLAMADA);
			equipara(TipoToken.PARENTESISABRE);
			Atributos atrL = L();
			equipara(TipoToken.PARENTESISCIERRA);
			
			if(atrId.esError() || atrL.esError())
				return Atributos.error();
			
			List<TipoDato> args;
			if(atrL.esVacio())
				args = new ArrayList<>();
			else
				args = atrL.getListaTipos();
			
			if(!atrId.esFuncion()) {
				reportarErrorSemantico(TipoError.LLAMADA_A_NO_FUNCION, idToken);
				return Atributos.error();
			}
			if(!semantico.comprobarFirma(atrId.posTS, args)) {
				reportarErrorSemantico(TipoError.FIRMA_INCORRECTA, idToken);
				return Atributos.error();
			}
			TipoDato tipoRet = atrId.getTipoRet();
			if(tipoRet == null || tipoRet == TipoDato.VOID)
				return Atributos.vacio();
			return Atributos.simple(tipoRet);
			
		}
		else if(Gramatica.esFollowY(tokenActual.tipo)) {//Y' -> lambda
			registrarRegla(ReglaGramatica.YPRIMA_LAMBDA);
			return Atributos.vacio();
		}
		else {
			reportarTokenInesperado();
			return Atributos.error();
		}
	}

	private Atributos S() {
		if(tokenActual.tipo == TipoToken.ID) {//S -> id S' ;
			registrarRegla(ReglaGramatica.S_ID);
			Token idToken = tokenActual;
			equipara(TipoToken.ID);
			Atributos atrId = semantico.registrarUsoIdentificador(idToken);
			idToken.posTS = atrId.posTS;
			Atributos atrSPrima = SPrima(atrId, idToken);
			equipara(TipoToken.PUNTOCOMA);
			
			return stmt(atrSPrima.esError()? TipoDato.ERROR: TipoDato.OK, null);
		}
		else if(tokenActual.tipo == TipoToken.WRITE) {//S -> write E ;
			registrarRegla(ReglaGramatica.S_WRITE);
			Token writeToken = tokenActual;
			equipara(TipoToken.WRITE);
			Atributos atrE = E();
			equipara(TipoToken.PUNTOCOMA);
			
			if(!atrE.esError() && atrE.esVacio())
				reportarErrorSemantico(TipoError.TIPOS_INCOMPATIBLES, writeToken);
			boolean ok = !atrE.esError() && !atrE.esVacio();
			return stmt(ok? TipoDato.OK: TipoDato.ERROR, null);
		}
		else if(tokenActual.tipo == TipoToken.READ) {//S -> read id ;
			registrarRegla(ReglaGramatica.S_READ);
			equipara(TipoToken.READ);
			Token idToken = tokenActual;
			equipara(TipoToken.ID);
			Atributos atrId = semantico.registrarUsoIdentificador(idToken);
			idToken.posTS = atrId.posTS;
			equipara(TipoToken.PUNTOCOMA);
			
			if(!atrId.esError() && (atrId.esVacio() || atrId.esFuncion()))
				reportarErrorSemantico(TipoError.TIPOS_INCOMPATIBLES, idToken);
			boolean ok = !atrId.esError() && !atrId.esVacio() && !atrId.esFuncion();
			return stmt(ok? TipoDato.OK: TipoDato.ERROR, null);
		}
		else if(tokenActual.tipo == TipoToken.RETURN) {//S -> return X ;
			registrarRegla(ReglaGramatica.S_RETURN);
			equipara(TipoToken.RETURN);
			Atributos atrX = X();
			equipara(TipoToken.PUNTOCOMA);
			
			TipoDato tipo = atrX.esError()? TipoDato.ERROR: TipoDato.OK;
			TipoDato tipoRet = atrX.esVacio()? TipoDato.VOID: atrX.getTipoDato();
			return stmt(tipo, tipoRet);
		}
		else {
			reportarTokenInesperado();
			return stmt(TipoDato.ERROR, null);
		}
	}

	private Atributos SPrima(Atributos atrId, Token idToken) {
		if(tokenActual.tipo == TipoToken.OPASIGNACION) {//S' -> = E
			registrarRegla(ReglaGramatica.SPRIMA_ASIG);
			equipara(TipoToken.OPASIGNACION);
			Atributos atrE = E();
			
			if(atrId.esError() || atrE.esError())
				return Atributos.error();
			if(atrId.esFuncion()) {
				reportarErrorSemantico(TipoError.LLAMADA_A_NO_FUNCION, idToken);
				return Atributos.error();
			}
			if(!CompatibilidadTipos.compatiblesAsignacion(atrId.getTipoDato(), atrE.getTipoDato())) {
				reportarErrorSemantico(TipoError.TIPOS_INCOMPATIBLES, idToken);
				return Atributos.error();
			}
			return Atributos.ok();
		}
		else if(tokenActual.tipo == TipoToken.OPASIGMULT) {//S' -> *= E
			registrarRegla(ReglaGramatica.SPRIMA_ASIGMULT);
			equipara(TipoToken.OPASIGMULT);
			Atributos atrE = E();
			
			if(atrId.esError() || atrE.esError())
				return Atributos.error();
			if(atrId.esFuncion()) {
				reportarErrorSemantico(TipoError.LLAMADA_A_NO_FUNCION, idToken);
				return Atributos.error();
			}
			if(!CompatibilidadTipos.compatiblesAsigMult(atrId.getTipoDato(), atrE.getTipoDato())) {
				reportarErrorSemantico(TipoError.TIPOS_INCOMPATIBLES, idToken);
				return Atributos.error();
			}
			return Atributos.ok();
		}
		else if(tokenActual.tipo == TipoToken.PARENTESISABRE) {//S' -> ( L )
			registrarRegla(ReglaGramatica.SPRIMA_LLAMADA);
			equipara(TipoToken.PARENTESISABRE);
			Atributos atrL = L();
			equipara(TipoToken.PARENTESISCIERRA);
			
			if(atrId.esError() || atrL.esError())
				return Atributos.error();
			
			List<TipoDato> args;
			if(atrL.esVacio())
				args = new ArrayList<>();
			else
				args = atrL.getListaTipos();
			
			if(!atrId.esFuncion()) {
				reportarErrorSemantico(TipoError.LLAMADA_A_NO_FUNCION, idToken);
				return Atributos.error();
			}
			if(!semantico.comprobarFirma(atrId.posTS, args)) {
				reportarErrorSemantico(TipoError.FIRMA_INCORRECTA, idToken);
				return Atributos.error();
			}
			return Atributos.ok();
		}
		else {
			reportarTokenInesperado();
			return Atributos.error();
		}
	}

	private Atributos L() {
		if(Gramatica.esFirstE(tokenActual.tipo)) {//L -> E Q
			registrarRegla(ReglaGramatica.L_EQ);
			Atributos atrE = E();
			Atributos atrQ = Q();
			
			if(atrE.esError() || atrQ.esError())
				return Atributos.error();
			
			List<TipoDato> tipos = new ArrayList<>();
			tipos.add(atrE.getTipoDato());
			if(!atrQ.esVacio())
				tipos.addAll(atrQ.getListaTipos());
			return Atributos.lista(tipos);
		}
		else if(tokenActual.tipo == TipoToken.PARENTESISCIERRA) {//L -> lambda, Follow(L) = (
			registrarRegla(ReglaGramatica.L_LAMBDA);
			return Atributos.vacio();
		}
		else {
			reportarTokenInesperado();
			return Atributos.error();
		}
	}

	private Atributos Q() {
		if(tokenActual.tipo == TipoToken.COMA) {//Q -> , E Q
			registrarRegla(ReglaGramatica.Q_COMA);
			equipara(TipoToken.COMA);
			Atributos atrE = E();
			Atributos atrQ2 = Q();
			
			if(atrE.esError() || atrQ2.esError())
				return Atributos.error();
			
			List<TipoDato> tipos = new ArrayList<>();
			tipos.add(atrE.getTipoDato());
			if(!atrQ2.esVacio())
				tipos.addAll(atrQ2.getListaTipos());
			return Atributos.lista(tipos);
		}
		else if(tokenActual.tipo == TipoToken.PARENTESISCIERRA) {//Q -> lambda, Follow(Q) = (
			registrarRegla(ReglaGramatica.Q_LAMBDA);
			return Atributos.vacio();
		}
		else {
			reportarTokenInesperado();
			return Atributos.error();
		}
	}

	private Atributos X() {
		if(Gramatica.esFirstE(tokenActual.tipo)) {//X -> E
			registrarRegla(ReglaGramatica.X_E);
			return E();
		}
		else if (tokenActual.tipo == TipoToken.PUNTOCOMA) {//X -> lambda, Follow(X) = ;
			registrarRegla(ReglaGramatica.X_LAMBDA);
			return Atributos.vacio();
		}
		else {
			reportarTokenInesperado();
			return Atributos.error();
		}
	}

	private Atributos B() {
		if(tokenActual.tipo == TipoToken.IF) {//B -> if ( E ) S
			registrarRegla(ReglaGramatica.B_IF);
			Token ifToken = tokenActual;
			equipara(TipoToken.IF);
			equipara(TipoToken.PARENTESISABRE);
			Atributos atrE = E();
			equipara(TipoToken.PARENTESISCIERRA);
			Atributos atrS = S();
			
			boolean condLogico = !atrE.esError() && atrE.getTipoDato() == TipoDato.LOGICO;
			if(!atrE.esError() && !condLogico)
				reportarErrorSemantico(TipoError.TIPOS_INCOMPATIBLES, ifToken);
			
			TipoDato tipo = (condLogico && !atrS.esError())? TipoDato.OK: TipoDato.ERROR;
			TipoDato tipoRet = condLogico? atrS.getTipoRet(): TipoDato.ERROR;
			return stmt(tipo, tipoRet);
		}
		else if(tokenActual.tipo == TipoToken.LET) {//B -> let T id B' ;
			registrarRegla(ReglaGramatica.B_LET);
			equipara(TipoToken.LET);
			Atributos atrT = T();
			Token idToken = tokenActual;
			equipara(TipoToken.ID);
			
			if(!atrT.esError()) {
				int pos = semantico.declararVariable(idToken, atrT.getTipoDato());
				idToken.posTS = pos;
			}
			
		 	Atributos atrBPrima = BPrima();
			equipara(TipoToken.PUNTOCOMA);
			
			boolean errorInit = false;
			if(!atrBPrima.esVacio() && !atrBPrima.esError() && !atrT.esError() && 
				!CompatibilidadTipos.compatiblesAsignacion(atrT.getTipoDato(), atrBPrima.getTipoDato())) {
					reportarErrorSemantico(TipoError.TIPOS_INCOMPATIBLES, idToken);
					errorInit = true;
				}
			
			TipoDato tipo = (atrBPrima.esError() || errorInit)? TipoDato.ERROR: TipoDato.OK;
			return stmt(tipo, null);
		}
		else if(Gramatica.esFirstS(tokenActual.tipo)) {//B -> S
			registrarRegla(ReglaGramatica.B_S);
			Atributos atrS = S();
			return stmt(atrS.esError()? TipoDato.ERROR: TipoDato.OK, atrS.getTipoRet());
		}
		else if(tokenActual.tipo == TipoToken.DO) {//B -> do { C } while ( E ) ;
			registrarRegla(ReglaGramatica.B_DO);
			Token doToken = tokenActual;
			equipara(TipoToken.DO);
			equipara(TipoToken.LLAVEABRE);
			Atributos atrC = C();
			equipara(TipoToken.LLAVECIERRA);
			equipara(TipoToken.WHILE);
			equipara(TipoToken.PARENTESISABRE);
			Atributos atrE = E();
			equipara(TipoToken.PARENTESISCIERRA);
			equipara(TipoToken.PUNTOCOMA);
			
			boolean condLogico = !atrE.esError() && atrE.getTipoDato() == TipoDato.LOGICO;
			if(!atrE.esError() && !condLogico)
				reportarErrorSemantico(TipoError.TIPOS_INCOMPATIBLES, doToken);
			boolean ok = !atrC.esError() && condLogico;
			return stmt(ok? TipoDato.OK: TipoDato.ERROR, null);
		}
		else {
			reportarTokenInesperado();
			return stmt(TipoDato.ERROR, null);
		}
	}

	private Atributos BPrima() {
		if(tokenActual.tipo == TipoToken.OPASIGNACION) {//B' -> = E
			registrarRegla(ReglaGramatica.BPRIMA_ASIG);
			equipara(TipoToken.OPASIGNACION);
			return E();
		}
		else if(tokenActual.tipo == TipoToken.PUNTOCOMA) {//B' -> lambda: Follow(B') = ;
			registrarRegla(ReglaGramatica.BPRIMA_LAMBDA);
			return Atributos.vacio();
		}
		else {
			reportarTokenInesperado();
			return Atributos.error();
		}
	}

	private Atributos T() {
		if(tokenActual.tipo == TipoToken.INT) {//T -> int
			registrarRegla(ReglaGramatica.T_INT);
			equipara(TipoToken.INT);
			return Atributos.simple(TipoDato.ENTERO);
		}
		else if(tokenActual.tipo == TipoToken.FLOAT) {//T -> float
			registrarRegla(ReglaGramatica.T_FLOAT);
			equipara(TipoToken.FLOAT);
			return Atributos.simple(TipoDato.REAL);
		}
		else if(tokenActual.tipo == TipoToken.BOOLEAN) {//T -> boolean
			registrarRegla(ReglaGramatica.T_BOOLEAN);
			equipara(TipoToken.BOOLEAN);
			return Atributos.simple(TipoDato.LOGICO);
		}
		else if(tokenActual.tipo == TipoToken.STRING) {//T -> string
			registrarRegla(ReglaGramatica.T_STRING);
			equipara(TipoToken.STRING);
			return Atributos.simple(TipoDato.CADENA);
		}
		else {
			reportarTokenInesperado();
			return Atributos.error();
		}
	}

	private Atributos F() {//F -> function H id ( A ) { C }
		registrarRegla(ReglaGramatica.F_FUNCION);
		equipara(TipoToken.FUNCTION);
		Atributos atrH = H();
		Token idToken = tokenActual;
		equipara(TipoToken.ID);
		
		TipoDato tipoRetDeclarado = atrH.esError()? TipoDato.VOID: atrH.getTipoDato();
		int pos = semantico.abrirFuncion(idToken, tipoRetDeclarado);
		idToken.posTS = pos;
		
		equipara(TipoToken.PARENTESISABRE);
		A();//En esta implementacion, A y K registran los parametros por su cuenta
		semantico.finalizarParametros();
		equipara(TipoToken.PARENTESISCIERRA);
		
		equipara(TipoToken.LLAVEABRE);
		Atributos atrC = C();
		equipara(TipoToken.LLAVECIERRA);
		
		if(!atrC.esError() && !atrH.esError()) {
			TipoDato tipoRetEncontrado = atrC.getTipoRet();
			if(tipoRetEncontrado != null 
					&& tipoRetEncontrado != TipoDato.OK
					&& tipoRetEncontrado != tipoRetDeclarado) {
				reportarErrorSemantico(TipoError.TIPO_RETORNO_INCORRECTO, idToken);
			}
		}
		semantico.cerrarFuncion();
		return atrC.esError()? Atributos.error(): Atributos.ok();
	}

	private Atributos H() {
		if(Gramatica.esFirstT(tokenActual.tipo)) {//H -> T
			registrarRegla(ReglaGramatica.H_T);
			return T();
		}
		else if(tokenActual.tipo == TipoToken.VOID) {//H -> void
			registrarRegla(ReglaGramatica.H_VOID);
			equipara(TipoToken.VOID);
			return Atributos.simple(TipoDato.VOID);
		}
		else {
			reportarTokenInesperado();
			return Atributos.error();
		}
	}

	private Atributos A() {
		if(Gramatica.esFirstT(tokenActual.tipo)) {//A -> T id K
			registrarRegla(ReglaGramatica.A_PARAM);
			Atributos atrT = T();
			Token idToken = tokenActual;
			equipara(TipoToken.ID);
			
			if(!atrT.esError()) {
				int pos = semantico.registrarParametro(atrT.getTipoDato(), idToken);
				idToken.posTS = pos;
			}
			
			K();
			return Atributos.ok();
		}
		else if(tokenActual.tipo == TipoToken.VOID) {//A -> void
			registrarRegla(ReglaGramatica.A_VOID);
			equipara(TipoToken.VOID);
			return Atributos.ok();
		}
		else {
			reportarTokenInesperado();
			return Atributos.error();
		}
	}

	private Atributos K() {
		if(tokenActual.tipo == TipoToken.COMA) {//K -> , T id K
			registrarRegla(ReglaGramatica.K_COMA);
			equipara(TipoToken.COMA);
			Atributos atrT = T();
			Token idToken = tokenActual;
			equipara(TipoToken.ID);
			
			if(!atrT.esError()) {
				int pos = semantico.registrarParametro(atrT.getTipoDato(), idToken);
				idToken.posTS = pos;
			}
			
			K();
			return Atributos.ok();
		}
		else if(tokenActual.tipo == TipoToken.PARENTESISCIERRA) {//K -> lambda: Follow(K) = )
			registrarRegla(ReglaGramatica.K_LAMBDA);
			return Atributos.ok();
		}
		else {
			reportarTokenInesperado();
			return Atributos.error();
		}
	}

	private Atributos C() {
		if(Gramatica.esFirstB(tokenActual.tipo)) {
			registrarRegla(ReglaGramatica.C_BC);
			Atributos atrB = B();
			Atributos atrC2 = C();
			
			TipoDato tipo;
			if(atrB.esError() || atrC2.esError())
				tipo = TipoDato.ERROR;
			else
				tipo = TipoDato.OK;
			
			TipoDato tipoRet;
			if(atrB.getTipoRet() != null && atrB.getTipoRet() != TipoDato.OK)
				tipoRet = atrB.getTipoRet();
			else
				tipoRet = atrC2.getTipoRet();
			return stmt(tipo, tipoRet);
		}
		else if(tokenActual.tipo == TipoToken.LLAVECIERRA){//C -> lambda: Follow(C) = {'}'}
			registrarRegla(ReglaGramatica.C_LAMBDA);
			return stmt(TipoDato.OK, TipoDato.OK);
		}
		else {
			reportarTokenInesperado();
			return stmt(TipoDato.ERROR, null);
		}
	}
	
	
	/** Crea un Atributos para sentencias y bloques
	 * @param tipo		OK o ERROR segun si la sentencia es valid
	 * @param tipoRet	tipo del return encontrado (null = sin return, OK = no aplica)
	 * @return
	 */
	private static Atributos stmt(TipoDato tipo, TipoDato tipoRet) {
		return new Atributos(tipo, TipoForma.VACIO, null, tipoRet);
	}

	private void registrarRegla(ReglaGramatica regla) {
		listaParse.add(regla);
	}
	
	
	/**Devuelve la lista de parse, en forma de numero de regla
	 * @return
	 */
	public List<Integer> getParseNumerico(){
		List<Integer> listaParseNum = new ArrayList<>();
		for(ReglaGramatica regla: listaParse) {
			listaParseNum.add(regla.numero);
		}
		return listaParseNum;
	}
	
	/**Devuelve la lista de parse, en forma de ReglaGramatica
	 * @return
	 */
	public List<ReglaGramatica> getParse(){
		return new ArrayList<>(listaParse);
	}
	
	
	/**Escribe la lista de reglas al fichero indicado
	 * Formato: un numero de regla por linea
	 * @param ruta
	 */
	public void escribirParse(Path ruta) {
		EscritorFichero.escribir(ruta, "descendente", getParseNumerico());
	}
	

	private void reportarTokenInesperado() {
		//Silenciamos errores repetidos, al subir las ramas del arbol
		if(ultimaLineaError == tokenActual.nLinea && ultimaColError == tokenActual.nCol)
			return;
		String lexema = tokenActual.lexema != null ? tokenActual.lexema : tokenActual.tipo.getSimbolo();
		gestorErrores.errorSintactico(TipoError.TOKEN_INESPERADO,
				tokenActual.nLinea, tokenActual.nCol, lexema, tokenActual.tipo.name());
		ultimaLineaError = tokenActual.nLinea;
		ultimaColError = tokenActual.nCol;
	}
	
	private void reportarTokenEsperado(TipoToken tipoEsperado) {
		gestorErrores.errorSintacticoEsperado(tokenActual.nLinea, tokenActual.nCol,
				tokenActual.tipo.name(), tokenActual.tipo.getSimbolo(), tipoEsperado.getSimbolo());
	}
	
	private void reportarFinalIncorrecto() {
		String lexema = tokenActual.lexema != null ? tokenActual.lexema : tokenActual.tipo.getSimbolo();
		gestorErrores.errorSintactico(TipoError.FINAL_INCORRECTO,
				tokenActual.nLinea, tokenActual.nCol, lexema, tokenActual.tipo.name());
	}
	
	private void reportarErrorSemantico(TipoError tipoError, Token token) {
		String lexema = token.lexema != null ? token.lexema : token.tipo.getSimbolo();
		gestorErrores.errorSemantico(tipoError,
				token.nLinea, token.nCol, lexema);
	}

}
