package es.upm.pdl.sintactico;

import java.util.Set;

import es.upm.pdl.lexico.TipoToken;


/**
 * Encapsula los FIRST y FOLLOW que usamos en el Analizador descendente recursivo.
 * Contiene metodos para comprobar si un TipoToken pertenece a un determinado conjunto
 */
public class Gramatica {
	
	private static final Set<TipoToken> FIRST_B = Set.of(
			TipoToken.IF,
			TipoToken.LET,
			TipoToken.DO,
			TipoToken.ID,
			TipoToken.WRITE,
			TipoToken.READ,
			TipoToken.RETURN
			);

	private static final Set<TipoToken> FIRST_S = Set.of(
			TipoToken.ID,
			TipoToken.WRITE,
			TipoToken.READ,
			TipoToken.RETURN

			);
	private static final Set<TipoToken> FIRST_E = Set.of(
			TipoToken.PARENTESISABRE,
			TipoToken.CTEENTERA,
			TipoToken.CTEREAL,
			TipoToken.CADENA,
			TipoToken.TRUE,
			TipoToken.FALSE,
			TipoToken.ID
			);

	private static final Set<TipoToken> FIRST_T = Set.of(
			TipoToken.INT,
			TipoToken.FLOAT,
			TipoToken.BOOLEAN,
			TipoToken.STRING
			);

	private static final Set<TipoToken> FOLLOW_E = Set.of(
			TipoToken.PARENTESISCIERRA,
			TipoToken.PUNTOCOMA,
			TipoToken.COMA
			);

	private static final Set<TipoToken> FOLLOW_U = Set.of(
			TipoToken.OPLOGICO,
			TipoToken.PARENTESISCIERRA,
			TipoToken.COMA,
			TipoToken.PUNTOCOMA
			);

	private static final Set<TipoToken> FOLLOW_V = Set.of(
			TipoToken.OPRELACIONAL,
			TipoToken.OPLOGICO,
			TipoToken.PARENTESISCIERRA,
			TipoToken.COMA,
			TipoToken.PUNTOCOMA
			);

	private static final Set<TipoToken> FOLLOW_Y = Set.of(
			TipoToken.OPARITMETICO,
			TipoToken.OPLOGICO,
			TipoToken.OPRELACIONAL,
			TipoToken.PARENTESISCIERRA,
			TipoToken.COMA,
			TipoToken.PUNTOCOMA
			);

	public static boolean esFirstB(TipoToken tipo) {return FIRST_B.contains(tipo);}
	public static boolean esFirstS(TipoToken tipo) {return FIRST_S.contains(tipo);}
	public static boolean esFirstE(TipoToken tipo) {return FIRST_E.contains(tipo);}
	public static boolean esFirstT(TipoToken tipo) {return FIRST_T.contains(tipo);}
	
	public static boolean esFollowE(TipoToken tipo) {return FOLLOW_E.contains(tipo);}
	public static boolean esFollowU(TipoToken tipo) {return FOLLOW_U.contains(tipo);}
	public static boolean esFollowV(TipoToken tipo) {return FOLLOW_V.contains(tipo);}
	public static boolean esFollowY(TipoToken tipo) {return FOLLOW_Y.contains(tipo);}
	
	
	

}
