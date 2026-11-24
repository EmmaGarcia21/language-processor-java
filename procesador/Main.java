package es.upm.pdl.procesador;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import es.upm.pdl.gestor_errores.GestorErrores;
import es.upm.pdl.lexico.Lexico;
import es.upm.pdl.sintactico.Sintactico;
import es.upm.pdl.tabla_simbolos.TS_Gestor;
import es.upm.pdl.utilidades.LectorFichero;

public class Main 
{
	private static final String RUTA_POR_DEFECTO = "./src/main/resources/pruebas";
	
    public static void main(String[] args) throws IOException{

    	Path dirBase;
    	if(args.length >= 1) {
    		dirBase = Paths.get(args[0]);
    	}
    	else {
    		dirBase = Paths.get(RUTA_POR_DEFECTO);
    	}
    	
    	Path rutaFuente = dirBase.resolve("entrada.javascript");
    	Path rutaSalida = dirBase;
    	
    	GestorErrores gestorErrores = new GestorErrores(true, Integer.MAX_VALUE);
		TS_Gestor gestorTS = new TS_Gestor(rutaSalida.resolve("ts.txt").toString());
		LectorFichero lectorFichero = new LectorFichero(rutaFuente);
		Lexico lexico = new Lexico(lectorFichero, gestorErrores);
		Sintactico sintactico = new Sintactico(lexico, gestorTS, gestorErrores);
    	try {
    		//Analisis
    		sintactico.ASint();
    		lectorFichero.close();
    	} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
    	finally {
    		//Ficheros de salida
    		lexico.escribirTokens(rutaSalida.resolve("tokens.txt"));
    		sintactico.escribirParse(rutaSalida.resolve("parse.txt"));
    		gestorErrores.escribirErrores(rutaSalida.resolve("errores.txt"));
    		
    		//Resumen por terminal
    		gestorErrores.imprimirResumen();
		}
    	
    }
}
