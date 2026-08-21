package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class AnalizadorPrioridad {
    private enum TipoToken {
        IDENTIFICADOR,
        ASIGNACION,
        OPERADOR_RELACIONAL,
        DELIMITADOR
    }

    private record Token(TipoToken tipo, String lexema) {
    }

    private record Operador(Token token, int longitud) {
    }

    public static void main(String[] args) {
        try (Scanner teclado = new Scanner(System.in)) {
            System.out.print("Ingrese una expresion: ");
            String entrada = teclado.nextLine();

            try {
                mostrarTokens(analizar(entrada));
            } catch (IllegalArgumentException error) {
                System.err.println("Error lexico: " + error.getMessage());
            }
        }
    }

    private static List<Token> analizar(String entrada) {
        List<Token> tokens = new ArrayList<>();
        int posicion = 0;

        while (posicion < entrada.length()) {
            char caracter = entrada.charAt(posicion);

            if (Character.isWhitespace(caracter)) {
                posicion++;
                continue;
            }

            if (Character.isLetter(caracter) || caracter == '_') {
                int inicio = posicion++;
                while (posicion < entrada.length()
                        && (Character.isLetterOrDigit(entrada.charAt(posicion))
                        || entrada.charAt(posicion) == '_')) {
                    posicion++;
                }
                tokens.add(new Token(TipoToken.IDENTIFICADOR,
                        entrada.substring(inicio, posicion)));
                continue;
            }

            Operador operador = reconocerOperador(entrada, posicion);
            if (operador != null) {
                tokens.add(operador.token());
                posicion += operador.longitud();
                continue;
            }

            if (caracter == ';') {
                tokens.add(new Token(TipoToken.DELIMITADOR, String.valueOf(caracter)));
                posicion++;
                continue;
            }

            throw new IllegalArgumentException(
                    "caracter no reconocido '" + caracter + "' en la posicion " + posicion);
        }

        return tokens;
    }

    private static Operador reconocerOperador(String entrada, int posicion) {
        // Primero se intenta la coincidencia valida mas larga.
        if (posicion + 1 < entrada.length()) {
            String candidatoLargo = entrada.substring(posicion, posicion + 2);
            if (candidatoLargo.equals("==") || candidatoLargo.equals(">=")
                    || candidatoLargo.equals("<=") || candidatoLargo.equals("!=")) {
                return new Operador(
                        new Token(TipoToken.OPERADOR_RELACIONAL, candidatoLargo), 2);
            }
        }

        char candidatoCorto = entrada.charAt(posicion);
        if (candidatoCorto == '>' || candidatoCorto == '<') {
            return new Operador(new Token(TipoToken.OPERADOR_RELACIONAL,
                    String.valueOf(candidatoCorto)), 1);
        }
        if (candidatoCorto == '=') {
            return new Operador(new Token(TipoToken.ASIGNACION,
                    String.valueOf(candidatoCorto)), 1);
        }
        return null;
    }

    private static void mostrarTokens(List<Token> tokens) {
        String borde = "+----------------------+----------------------+";
        System.out.println(borde);
        System.out.printf("| %-20s | %-20s |%n", "TOKEN", "LEXEMA");
        System.out.println(borde);
        for (Token token : tokens) {
            System.out.printf("| %-20s | %-20s |%n", token.tipo(), token.lexema());
        }
        System.out.println(borde);
    }
}
