package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class AnalizadorIntegral {
    public static void main(String[] args) {
        Path archivo = Path.of(args.length > 0 ? args[0] : "programa_integral.txt");
        try {
            String codigo = Files.readString(archivo);
            LexerCompleto.Resultado resultado = new LexerCompleto().analizar(codigo);
            LexerCompleto.mostrarTokens(resultado);
            LexerCompleto.mostrarErrores(resultado);
            System.out.println("Cantidad total de tokens: " + resultado.tokens().size());
            System.out.println("Operadores: se aplica la coincidencia valida mas larga.");
            System.out.println("Numeros: los que poseen punto son decimales; los demas, enteros.");
            System.out.println("Comentarios: se conservan como un unico token COMENTARIO.");
        } catch (IOException error) {
            System.err.println("No se pudo leer '" + archivo + "': " + error.getMessage());
        }
    }
}
