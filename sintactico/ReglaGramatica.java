package es.upm.pdl.sintactico;

public enum ReglaGramatica {
	
	P_BP		(1),//P -> B P
	P_FP		(2),//P -> F P
	P_LAMBDA	(3),//P -> lambda
	
	E_UEPRIMA		(4),//E -> U EPrima
	EPRIMA_AND		(5),//EPrima -> && U EPrima
	EPRIMA_LAMBDA	(6),//EPrima -> lambda
	
	U_VUPRIMA		(7),//U -> V UPrima
	UPRIMA_IGUAL	(8),//UPrima -> == V UPrima
	UPRIMA_LAMBDA	(9),//UPrima -> lambda
	
	V_YVPRIMA		(10),//V -> Y VPrima
	VPRIMA_MULT		(11),//VPrima -> * Y VPrima
	VPRIMA_LAMBDA	(12),//VPrima -> lambda
	
	Y_PAREN		(13),//Y -> ( E )
	Y_ENTERA	(14),//Y -> cteentera
	Y_REAL		(15),//Y -> ctereal
	Y_CADENA	(16),//Y -> cadena
	Y_TRUE		(17),//Y -> true
	Y_FALSE		(18),//Y -> false
	Y_ID		(19),//Y -> id YPrima
	
	YPRIMA_LLAMADA	(20),//YPrima -> ( L )
	YPRIMA_LAMBDA	(21),//YPrima -> lambda
	
	S_ID		(22),//S -> id SPrima ;
	S_WRITE		(23),//S -> write E ;
	S_READ		(24),//S -> read id ;
	S_RETURN	(25),//S -> return X ;
	
	SPRIMA_ASIG		(26),//SPrima -> = E
	SPRIMA_ASIGMULT	(27),//SPrima -> *= E
	SPRIMA_LLAMADA	(28),//SPrima -> ( L )
	
	L_EQ		(29),//L -> E Q
	L_LAMBDA	(30),//L -> lambda
	
	Q_COMA		(31),//Q -> , E Q
	Q_LAMBDA	(32),//Q -> lambda
	
	X_E			(33),//X -> E
	X_LAMBDA	(34),//X -> lambda
	
	B_IF		(35),//B -> if ( E ) S
	B_LET		(36),//B -> let T id BPrima ;
	B_S			(37),//B -> S
	B_DO		(38),//B -> do { C } while ( E ) ;
	
	BPRIMA_ASIG		(39),//BPrima -> = E
	BPRIMA_LAMBDA	(40),//BPrima -> lambda
	
	T_INT		(41),//T -> int
	T_FLOAT		(42),//T -> float
	T_BOOLEAN	(43),//T -> boolean
	T_STRING	(44),//T -> string
	
	F_FUNCION	(45),//F -> function H id ( A ) { C }
	
	H_T			(46),//H -> T
	H_VOID		(47),//H -> void
	
	A_PARAM		(48),//A -> T id K
	A_VOID		(49),//A -> void
	
	K_COMA		(50),//K -> , T id K
	K_LAMBDA	(51),//K -> lambda
	
	C_BC		(52),//C -> B C
	C_LAMBDA	(53);//C -> lambda
	
	public final int numero;
	
	private ReglaGramatica(int numero) {
		this.numero = numero;
	}
	
	@Override
	public String toString() {
		return numero + " (" + name() + ")";
	}
}
