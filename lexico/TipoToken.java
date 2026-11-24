package es.upm.pdl.lexico;

public enum TipoToken {
	CTEENTERA		("cte entera"),		//<CTEENTERA, valor>
	CTEREAL			("cte real"),		//<CTEREAL, valor>
	CADENA			("cadena"),			//<CADENA, lexema>
	TRUE			("true"),			//<TRUE, ->				true
	FALSE			("false"),			//<FALSE, ->			false
	
	OPARITMETICO	("*"),				//<OPARITMETICO, ->		*
	OPRELACIONAL	("=="),				//<OPRELACIONAL, ->		==
	OPLOGICO		("&&"),				//<OPLOGICO, ->			&&
	OPASIGNACION	("="),				//<OPASIGNACION, ->		=
	OPASIGMULT		("*="),				//<OPASIGMULT, -> 		*=
	
	ID				("identificador"),	//<ID, punteroTablaDeSimbolos>
	
	INT				("int"),			//<INT, ->				int
	FLOAT			("float"),			//<FLOAT, ->			float
	STRING			("string"),			//<STRING, ->			string
	BOOLEAN 		("boolean"),		//<BOOLEAN, ->			boolean
	VOID			("void"),			//<VOID, ->				void
	LET				("let"),			//<LET, ->				let
	IF				("if"),				//<IF, ->				if
	DO				("do"),				//<DO, ->				do
	WHILE			("while"),			//<WHILE, ->			while
	FUNCTION		("function"),		//<FUNCTION, ->			function
	RETURN			("return"),			//<RETURN, ->			return
	WRITE			("write"),			//<WRITE, ->			write
	READ			("read"),			//<READ, ->				read
	PARENTESISABRE	("("),				//<PARENTESISABRE, ->	(
	PARENTESISCIERRA(")"),				//<PARENTESISCIERRA, ->	)
	LLAVEABRE		("{"),				//<LLAVEABRE, ->		{
	LLAVECIERRA		("}"),				//<LLAVECIERRA, ->		}
	PUNTOCOMA		(";"),				//<PUNTOCOMA, ->		;
	COMA			(","),				//<COMA, ->				,
	
	EOF				("End Of File");	//<EOF, ->
	
	private String simbolo;
	
	private TipoToken(String simbolo) {
		this.simbolo = simbolo;
	}
	
	public String getSimbolo() {
		return simbolo;
	}
}
