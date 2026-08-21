package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class AnalizadorCadenas {
    private enum TipoToken {
        IDENTIFICADOR,
        ASIGNACION,
        CADENA,
        DELIMITADOR
    }

    private record Token(TipoToken tipo, String lexema) {
    }

    public static void main(String[] args) {
        try (Scanner teclado = new Scanner(System.in)) {
            System.out.print("Ingrese una asignacion de cadena: ");
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

            if (caracter == '=') {
                tokens.add(new Token(TipoToken.ASIGNACION, String.valueOf(caracter)));
                posicion++;
                continue;
            }

            if (caracter == '"') {
                int inicio = posicion++;
                boolean cerrada = false;

                while (posicion < entrada.length()) {
                    if (entrada.charAt(posicion) == '\\' && posicion + 1 < entrada.length()) {
                        posicion += 2;
                    } else if (entrada.charAt(posicion) == '"') {
                        posicion++;
                        cerrada = true;
                        break;
                    } else {
                        posicion++;
                    }
                }

                if (!cerrada) {
                    throw new IllegalArgumentException(
                            "cadena sin comilla de cierre en la posicion " + inicio);
                }

                tokens.add(new Token(TipoToken.CADENA,
                        entrada.substring(inicio, posicion)));
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
