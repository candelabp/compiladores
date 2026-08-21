package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class AnalizadorRelacional {
    private enum TipoToken {
        IDENTIFICADOR,
        OPERADOR_RELACIONAL,
        NUMERO,
        DELIMITADOR
    }

    private record Token(TipoToken tipo, String lexema) {
    }

    public static void main(String[] args) {
        try (Scanner teclado = new Scanner(System.in)) {
            System.out.print("Ingrese una expresion relacional: ");
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

            if (Character.isDigit(caracter)) {
                int inicio = posicion++;
                while (posicion < entrada.length()
                        && Character.isDigit(entrada.charAt(posicion))) {
                    posicion++;
                }
                tokens.add(new Token(TipoToken.NUMERO,
                        entrada.substring(inicio, posicion)));
                continue;
            }

            if (caracter == '>' || caracter == '<' || caracter == '=' || caracter == '!') {
                int inicio = posicion++;

                if (posicion < entrada.length() && entrada.charAt(posicion) == '=') {
                    posicion++;
                    tokens.add(new Token(TipoToken.OPERADOR_RELACIONAL,
                            entrada.substring(inicio, posicion)));
                    continue;
                }

                if (caracter == '>' || caracter == '<') {
                    tokens.add(new Token(TipoToken.OPERADOR_RELACIONAL,
                            String.valueOf(caracter)));
                    continue;
                }

                throw new IllegalArgumentException(
                        "operador relacional incompleto en la posicion " + inicio);
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
