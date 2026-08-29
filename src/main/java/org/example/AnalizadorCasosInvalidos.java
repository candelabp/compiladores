package org.example;

import java.util.List;

public class AnalizadorCasosInvalidos {
    private static final List<String> CASOS = List.of(
            "123abc", "@usuario", "\"Hola", "12.5.8", "A === B", "#contador");

    public static void main(String[] args) {
        List<String> entradas = args.length == 0 ? CASOS : List.of(String.join(" ", args));
        LexerCompleto analizador = new LexerCompleto();

        for (String entrada : entradas) {
            System.out.println("Entrada: " + entrada);
            LexerCompleto.Resultado resultado = analizador.analizar(entrada);
            LexerCompleto.mostrarTokens(resultado);
            LexerCompleto.mostrarErrores(resultado);
            System.out.println();
        }
    }
}
